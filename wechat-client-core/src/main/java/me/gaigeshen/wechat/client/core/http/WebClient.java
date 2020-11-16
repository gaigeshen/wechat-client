package me.gaigeshen.wechat.client.core.http;

import me.gaigeshen.wechat.client.core.util.Asserts;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.ContentResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author gaigeshen
 */
public class WebClient implements Closeable {

  private final CloseableHttpClient client;

  public static Builder builder() {
    return Builder.create();
  }

  private WebClient(int connectionRequestTimeout, int connectTimeout, int socketTimeout, SSLContext sslContext) {
    RequestConfig config = RequestConfig.custom()
            .setConnectionRequestTimeout(connectionRequestTimeout)
            .setConnectTimeout(connectTimeout)
            .setSocketTimeout(socketTimeout)
            .build();

    SSLConnectionSocketFactory sslConnSocFactory = sslContext != null
            ? new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)
            : SSLConnectionSocketFactory.getSocketFactory();

    Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", sslConnSocFactory)
            .build();

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(sfr);
    connectionManager.setDefaultMaxPerRoute(20); // Set the maximum number of concurrent connections per route, which is 2 by default
    connectionManager.setMaxTotal(200); // Set the maximum number of total open connections
    connectionManager.setValidateAfterInactivity(1000);

    this.client = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setKeepAliveStrategy(new KeepAliveStrategy())
            .evictExpiredConnections()
            .evictIdleConnections(1800, TimeUnit.SECONDS)
            .setDefaultRequestConfig(config)
            .build();
  }

  public ResponseContent execute(RequestContent req) throws WebClientException {
    if ("get".equalsIgnoreCase(req.getMethod())) {
      return execute(createHttpGet(req));
    } else if ("post".equalsIgnoreCase(req.getMethod())) {
      return execute(createHttpPost(req));
    }
    throw new InvalidRequestContentException("Can only support 'get' and 'post' method:: " + req);
  }

  private HttpGet createHttpGet(RequestContent req) throws InvalidRequestContentException {
    try {
      return new HttpGet(req.getUri());
    } catch (Exception e) {
      throw new InvalidRequestContentException("Invalid uri:: " + req, e);
    }
  }

  private HttpPost createHttpPost(RequestContent req) throws InvalidRequestContentException {
    HttpPost post = new HttpPost(req.getUri());
    if (Objects.nonNull(req.getMultipartParameters())) {
      boolean validMultipart = false;
      MultipartEntityBuilder multipartBuilder = MultipartEntityBuilder.create();
      for (Map.Entry<String, Object> entry : req.getMultipartParameters().entrySet()) {
        if (entry.getValue() instanceof byte[]) {
          multipartBuilder.addBinaryBody(entry.getKey(), (byte[]) entry.getValue());
          validMultipart = true;
        }
        if (entry.getValue() instanceof File) {
          multipartBuilder.addBinaryBody(entry.getKey(), (File) entry.getValue());
          validMultipart = true;
        }
        if (entry.getValue() instanceof InputStream) {
          multipartBuilder.addBinaryBody(entry.getKey(), (InputStream) entry.getValue());
          validMultipart = true;
        }
        if (entry.getValue() instanceof String) {
          multipartBuilder.addTextBody(entry.getKey(), (String) entry.getValue());
          validMultipart = true;
        }
      }
      if (!validMultipart) {
        throw new InvalidRequestContentException("Invalid multipart parameters:: " + req);
      }
      post.setEntity(multipartBuilder.build());
      return post;
    }
    if (Objects.isNull(req.getType())) {
      throw new InvalidRequestContentException("Request content type can not be null::");
    }
    ContentType contentType = req.getType().parseToContentType(req.getEncoding());
    EntityBuilder builder = EntityBuilder.create().setContentType(contentType).setContentEncoding(req.getEncoding());
    if (Objects.nonNull(req.getText())) {
      builder.setText(req.getText());
    } else if (Objects.nonNull(req.getBinary())) {
      builder.setBinary(req.getBinary());
    } else if (Objects.nonNull(req.getStream())) {
      builder.setStream(req.getStream());
    } else if (Objects.nonNull(req.getParameters())) {
      List<NameValuePair> nameValuePairs = new ArrayList<>();
      for (Map.Entry<String, String> entry : req.getParameters().entrySet()) {
        nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
      }
      builder.setParameters(nameValuePairs);
    } else {
      throw new InvalidRequestContentException("Could not find request content data:: " + req);
    }
    post.setEntity(builder.build());
    return post;
  }

  public ResponseContent execute(HttpUriRequest req) throws WebClientException {
    Content content = execute(req, new ContentResponseHandler());
    return new ResponseContent() {
      @Override
      public byte[] getRawBytes() {
        return content.asBytes();
      }
      @Override
      public String getType() {
        return content.getType().getMimeType();
      }
      @Override
      public String getAsString() {
        return content.asString();
      }
      @Override
      public String getAsString(Charset charset) {
        return content.asString(charset);
      }
      @Override
      public InputStream getAsStream() {
        return content.asStream();
      }
    };
  }

  public <T> T execute(HttpUriRequest req, AbstractResponseHandler<T> handler) throws WebClientException {
    try {
      return client.execute(req, handler);
    } catch (IOException e) {
      throw new WebClientException("Could not execute request:: " + req, e);
    }
  }

  @Override
  public void close() throws IOException {
    client.close();
  }

  /**
   * @author gaigeshen
   */
  public static class Builder {
    private int connectionRequestTimeout = 1000;
    private int connectTimeout = 2000;
    private int socketTimeout = 5000;
    private SSLContext sslContext = null;

    public static Builder create() {
      return new Builder();
    }

    public Builder setConnectionRequestTimeout(int connectionRequestTimeout) {
      this.connectionRequestTimeout = Asserts.positive(connectionRequestTimeout, "connectionRequestTimeout");
      return this;
    }

    public Builder setConnectTimeout(int connectTimeout) {
      this.connectTimeout = Asserts.positive(connectTimeout, "connectTimeout");
      return this;
    }

    public Builder setSocketTimeout(int socketTimeout) {
      this.socketTimeout = Asserts.positive(socketTimeout, "socketTimeout");
      return this;
    }

    public Builder setSslContext(SSLContext sslContext) {
      this.sslContext = sslContext;
      return this;
    }

    public WebClient build() {
      return new WebClient(connectionRequestTimeout, connectTimeout, socketTimeout, sslContext);
    }
  }

  /**
   * @author gaigeshen
   */
  private static class KeepAliveStrategy implements ConnectionKeepAliveStrategy {
    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
      HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
      while (it.hasNext()) {
        HeaderElement headerElement = it.nextElement();
        String name = headerElement.getName();
        String value = headerElement.getValue();
        if (value != null && name.equalsIgnoreCase("timeout")) {
          try {
            return Long.parseLong(value) * 1000;
          } catch (NumberFormatException ignored) {
          }
        }
      }
      return 65 * 1000;
    }
  }
}
