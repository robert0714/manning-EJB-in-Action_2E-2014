package com.actionbazaar.chapter5.listing05;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.criteria.Order;

import com.actionbazaar.chapter5.listing04.SayHelloInterceptor;
/**
 * If EJB's Simple Name (com.actionbazaar.chapter5.listing05.OrderBean)  are the same as another (com.actionbazaar.chapter5.listing06.OrderBean)<br/>
 * It is  conflict between them!!
 * **/
@Stateless
public class OrderBeanV1 {
  @Interceptors(SayHelloInterceptor.class)
  public Order findOrderById(String id) { return null; }
}
