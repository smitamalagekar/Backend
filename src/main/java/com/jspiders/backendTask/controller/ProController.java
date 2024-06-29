package com.jspiders.backendTask.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jspiders.backendTask.Repository.ProRepository;
import com.jspiders.backendTask.entity.Product;

import jakarta.persistence.criteria.CriteriaBuilder.In;

@RestController
public class ProController {
	
	@Autowired
	private ProRepository proRepository;
	
	@GetMapping(path = "/products-a")
	public List<Product> addProduct(){
		
		RestTemplate restTemplate=new RestTemplate();
		Product[] arr= restTemplate.getForObject("https://s3.amazonaws.com/roxiler.com/product_transaction.json", Product[].class);
		List<Product> products=new ArrayList<>();
	    for (int i=0;i<arr.length;i++) {
			products.add(arr[i]);
		}		
		return proRepository.saveAll(products);
	}
	
	@GetMapping(path = "/products/{search}")
	public List<Product> findTheProducts(@PathVariable String search){
		try {
			double price=Double.parseDouble(search);
			return proRepository.findTheProuctByPrice(price);
		} catch (Exception e) {
			return proRepository.findTheProductByTitleOrDescription(search);
		}
		
	}
	
	@GetMapping(path="/stastics")
	public double[] fectTheStastics(@RequestParam(name="month") String month){
		double totalSaleAmount=0;
		int soldItems=0;
		int unsoldItems=0;
		List<Product> products=proRepository.findAll();
		for (Product product : products) {
			if( product.getDateOfSale().contains("-"+month+"-")){
				if(product.isSold()) {
					soldItems++;
					totalSaleAmount+=product.getPrice();
				}else {
					unsoldItems++;
				}
			}
		}
		double[] arr= {totalSaleAmount,soldItems,unsoldItems};
		return arr;
	    
		
	}
	
	@GetMapping(path = "/bar-chart")
	public Map<String, Integer> fetchTheBarChart(@RequestParam (name="month") String month){
		int arr[]=new int[10];
		List<Product> products=proRepository.findAll();
		for (Product product : products) {
			if (product.getDateOfSale().contains("-"+month+"-")) {
				double price =product.getPrice();
				if (price>=0 && price<=100 ) {
					++arr[0];
				}else if (price>=101 && price<=200 ) {
					++arr[1];
				}else if (price>=201 && price<=300 ) {
					++arr[2];
				}else if (price>=301 && price<=400 ) {
					++arr[3];
				}else if (price>=401 && price<=500 ) {
					++arr[4];
				}else if (price>=501 && price<=600 ) {
					++arr[5];
				}else if (price>=601 && price<=700 ) {
					++arr[6];
				}else if (price>=701 && price<=800 ) {
					++arr[7];
				}else if (price>=801 && price<=900 ) {
					++arr[8];
				}else if (price>=901) {
					++arr[9];
				}
				
			}
		}
		Map<String, Integer> map=new HashMap<>();
		map.put("0-100",arr[0]);
		map.put("101-200",arr[1]);
		map.put("201-300",arr[2]);
		map.put("301-400",arr[3]);
		map.put("401-500",arr[4]);
		map.put("501-600",arr[5]);
		map.put("601-700",arr[6]);
		map.put("701-800",arr[7]);
		map.put("801-900",arr[8]);
		map.put("901 and above",arr[9]);
		return map;
		
	}
	
	@GetMapping(path="/pie_chart")
	public Map<String, Integer> fetchTheCategories(@RequestParam(name="month") String month){
		
		
		int[] arr=new int[4];
		List<Product> products=proRepository.findAll();
		for (Product product : products) {
			if (product.getDateOfSale().contains("-"+month+"-")) {
				String category =product.getCategory();
				if(category.equals("men's clothing")) {
					arr [0] = ++arr[0];
				}else if (category.equals("women's clothing")) {
					arr [1] = ++arr[1];
				}else if (category.equals("electronics")){
					arr[2]=++arr[2];
				}else if (category.equals("jewelery")) {
					arr[3]= ++arr[3];
				}
			}
			
		}
	     Map<String, Integer> map=new HashMap<>();
	     map.put("men's clothing", arr[0]);
	     map.put("women's clothing", arr[1]);
	     map.put("electronics", arr[2]);
	     map.put("jewelery", arr[3]);
		return map;		
	}


	
	   @GetMapping(path = "/allData")
	    public List<Object> FetchAllData(@RequestParam(name = "month") String month) {
	        List<Object> list = new ArrayList<>();
	        list.add(fectTheStastics(month));
	        list.add(fetchTheBarChart(month));
	        return list;
	    }

}