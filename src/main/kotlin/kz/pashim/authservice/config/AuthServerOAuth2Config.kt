package kz.pashim.authservice.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.jdbc.datasource.init.DatabasePopulator
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import javax.sql.DataSource

@Configuration
@EnableAuthorizationServer
class AuthServerOAuth2Config : AuthorizationServerConfigurerAdapter() {

    @Value("classpath:schema.sql")
    private lateinit var schemaScript: Resource

    @Autowired
    @Qualifier("authenticationManagerBean")
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var dataSource: DataSource

    @Throws(Exception::class)
    override fun configure(
            oauthServer: AuthorizationServerSecurityConfigurer?) {
        oauthServer!!
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
    }

    @Throws(Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer?) {
        clients!!.jdbc(dataSource)
                .withClient("sampleClientId")
                .authorizedGrantTypes("implicit")
                .scopes("read")
                .autoApprove(true)
                .and()
                .withClient("clientIdPassword")
                .secret("secret")
                .authorizedGrantTypes(
                        "password", "authorization_code", "refresh_token")
                .scopes("read")
    }

    @Throws(Exception::class)
    override fun configure(
            endpoints: AuthorizationServerEndpointsConfigurer?) {

        endpoints!!
                .tokenStore(tokenStore())
                .authenticationManager(authenticationManager)
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JdbcTokenStore(dataSource)
    }

    @Bean
    fun dataSourceInitializer(dataSource: DataSource): DataSourceInitializer {
        val initializer = DataSourceInitializer()
        initializer.setDataSource(dataSource)
        initializer.setDatabasePopulator(databasePopulator())
        return initializer
    }

    private fun databasePopulator(): DatabasePopulator {
        val populator = ResourceDatabasePopulator()
        populator.addScript(schemaScript)
        return populator
    }
}