package org.example.config.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.h2.tools.Server;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("h2")
public class ConnectionFactory {

  private static final String URL = "jdbc:h2:./banco/cashflow;AUTO_SERVER=TRUE";
  private static final String USER = "sa";
  private static final String PASSWORD = "";

  @Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
      .driverClassName("org.h2.Driver")
      .url(URL)
      .username(USER)
      .password(PASSWORD)
      .build();
  }

  public static Connection getConnection() {
    try {
      return DriverManager.getConnection(URL, USER, PASSWORD);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao conectar no banco de dados H2!", e);
    }
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Server h2WebConsoleServer() throws SQLException {
    System.out.println("Iniciando console do H2 na porta 8082...");
    return Server.createWebServer(
      "-web",
      "-webAllowOthers",
      "-webPort",
      "8082"
    );
  }
}
