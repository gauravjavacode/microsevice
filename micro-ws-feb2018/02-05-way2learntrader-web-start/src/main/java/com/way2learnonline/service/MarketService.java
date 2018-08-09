package com.way2learnonline.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.way2learnonline.domain.CompanyInfo;
import com.way2learnonline.domain.Order;
import com.way2learnonline.domain.Portfolio;
import com.way2learnonline.domain.Quote;
import com.way2learnonline.exception.OrderNotSavedException;




@Service
@RefreshScope
public class MarketService {
	private static final Logger logger = LoggerFactory.getLogger(MarketService.class);

	@Autowired	
	private RestTemplate restTemplate;

    @Value("${pivotal.quotesService.name:quotes-service}")
	private String quotesService;
	
    @Value("${pivotal.portfolioService.name:portfolio-service}")
	private String portfolioService;
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    
    public URI getServiceURI(String serviceName){
		List<ServiceInstance> instances=  discoveryClient.getInstances(serviceName);
		URI uri=instances.get(0).getUri();
		System.err.println(serviceName+" URI : "+uri);
		return uri;
	}

	//@HystrixCommand(fallbackMethod = "getCompaniesFallback")
	public List<CompanyInfo> getCompanies(String name) {
		logger.debug("Fetching companies with name or symbol matching: " + name);
		
		CompanyInfo[] infos = restTemplate.getForObject(getServiceURI(quotesService) + "/company/{name}", CompanyInfo[].class, name);
		//return Arrays.asList(infos);
		List<CompanyInfo> list=Arrays.asList(infos);
		return list;
		
		
	}

	@SuppressWarnings("unused")
	private List<CompanyInfo> getCompaniesFallback(String name) {
        return new ArrayList<>();
	}

	/**
	 * Retrieve multiple quotes.
	 * 
	 * @param symbols comma separated list of symbols.
	 * @return
	 */
   
	public List<Quote> getQuotes(String symbols) {
		logger.debug("retrieving multiple quotes: " + symbols);
		Quote[] quotesArr = restTemplate.getForObject(getServiceURI(quotesService)+ "/quotes?q={symbols}", Quote[].class, symbols);
		List<Quote> quotes = Arrays.asList(quotesArr);
		logger.debug("Received quotes: {}",quotes);
		return quotes;
	}

    @SuppressWarnings("unused")
    private List<Quote> getQuotesFallback(String symbols) {
        List<Quote> result = new ArrayList<>();
        String[] splitSymbols = symbols.split(",");

        for (String symbol : splitSymbols) {
            Quote quote = new Quote();
            quote.setSymbol(symbol);
            quote.setStatus("FAILED");
            result.add( quote );
        }
        return result;
    }

	public List<Quote> getQuotes(String[] symbols) {
		logger.debug("Fetching multiple quotes array: {} ",Arrays.asList(symbols));
		
		return getQuotes(Arrays.asList(symbols));
	}

	public List<Quote> getQuotes(Collection<String> symbols) {
		logger.debug("Fetching multiple quotes array: {} ",symbols);
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> i = symbols.iterator(); i.hasNext();) {
			builder.append(i.next());
			if (i.hasNext()) {
				builder.append(",");
			}
		}
		return getQuotes(builder.toString());
	}

    public Order sendOrder(Order order ) throws OrderNotSavedException{
		logger.debug("send order: " + order);
		
		//check result of http request to ensure its ok.
		
		ResponseEntity<Order>  result = restTemplate.postForEntity(getServiceURI(portfolioService)+ "/portfolio/{accountId}", order, Order.class, order.getAccountId());
		if (result.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			throw new OrderNotSavedException("Could not save the order");
		}
		logger.debug("Order saved:: " + result.getBody());
		return result.getBody();
	}

	public Portfolio getPortfolio(String accountId) {
		Portfolio folio = restTemplate.getForObject(getServiceURI(portfolioService)+ "/portfolio/{accountid}", Portfolio.class, accountId);
		logger.debug("Portfolio received: " + folio);
		return folio;
	}

	@SuppressWarnings("unused")
	private Portfolio getPortfolioFallback(String accountId) {
		logger.debug("Portfolio fallback");
		Portfolio folio = new Portfolio();
		folio.setAccountId(accountId);
		return folio;
	}
	
	

}
