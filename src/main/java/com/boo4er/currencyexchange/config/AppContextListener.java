package com.boo4er.currencyexchange.config;

import com.boo4er.currencyexchange.dao.CurrencyDao;
import com.boo4er.currencyexchange.dao.ExchangeRateDao;
import com.boo4er.currencyexchange.dao.JdbcCurrencyDao;
import com.boo4er.currencyexchange.dao.JdbcExchangeRateDao;
import com.boo4er.currencyexchange.exception.DatabaseException;
import com.boo4er.currencyexchange.service.CurrencyService;
import com.boo4er.currencyexchange.service.ExchangeRateService;
import com.boo4er.currencyexchange.util.DatabaseUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DatabaseUtil.init();
            log.info("База данных инициализирована при старте приложения");
        } catch (DatabaseException e) {
            log.error("Не удалось инициализировать БД", e);
            throw new IllegalStateException("Не удалось инициализировать базу данных при запуске", e);
        }
        registerServices(sce.getServletContext());
    }

    private void registerServices(ServletContext servletContext) {
        CurrencyDao currencyDao = new JdbcCurrencyDao();
        ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

        CurrencyService currencyService = new CurrencyService(currencyDao);
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao, currencyService);

        servletContext.setAttribute(CurrencyService.class.getName(), currencyService);
        servletContext.setAttribute(ExchangeRateService.class.getName(), exchangeRateService);
        log.info("Сервисы зарегестрированы");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseUtil.close();
        log.info("Приложение остановлено");
    }
}
