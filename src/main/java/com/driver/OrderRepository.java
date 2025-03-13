package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<String, Order>();
        this.partnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
        this.orderToPartnerMap = new HashMap<String, String>();
    }

    public void saveOrder(Order order){
        // your code here
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId){
        // your code here
        partnerMap.put(partnerId, new DeliveryPartner(partnerId));
        // create a new partner with given partnerId and save it
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            // your code here
            partnerToOrderMap.putIfAbsent(partnerId, new HashSet<>());
            partnerToOrderMap.get(partnerId).add(orderId);
            orderToPartnerMap.put(orderId, partnerId);

            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);
            //add order to given partner's order list
            //increase order count of partner
            //assign partner to this order
        }
    }

    public Order findOrderById(String orderId){
        // your code here
        return orderMap.get(orderId);
    }

    public DeliveryPartner findPartnerById(String partnerId){
        // your code here
        return partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        // your code here
        return partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()).size();
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        // your code here
        return new ArrayList<>(partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()));
    }

    public List<String> findAllOrders(){
        // your code here
        // return list of all orders
        return new ArrayList<>(orderMap.keySet());

    }

    public void deletePartner(String partnerId){
        // your code here
        if (partnerToOrderMap.containsKey(partnerId)) {
            for (String orderId : partnerToOrderMap.get(partnerId)) {
                orderToPartnerMap.remove(orderId);
            }
        }
        partnerToOrderMap.remove(partnerId);
        partnerMap.remove(partnerId);
        // delete partner by ID
    }

    public void deleteOrder(String orderId){
        // your code here
        if (orderToPartnerMap.containsKey(orderId)) {
            String partnerId = orderToPartnerMap.get(orderId);
            partnerToOrderMap.get(partnerId).remove(orderId);
            orderToPartnerMap.remove(orderId);

            DeliveryPartner partner = partnerMap.get(partnerId);
            if (partner != null) {
                partner.setNumberOfOrders(partner.getNumberOfOrders() - 1);
            }
        }
        orderMap.remove(orderId);

        // delete order by ID

    }

    public Integer findCountOfUnassignedOrders(){
        // your code here
        return (int) orderMap.keySet().stream()
                .filter(orderId -> !orderToPartnerMap.containsKey(orderId))
                .count();

    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        // your code here
        if (!partnerToOrderMap.containsKey(partnerId)) return 0;

        String[] timeParts = timeString.split(":");
        int givenTime = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);

        return (int) partnerToOrderMap.get(partnerId).stream()
                .map(orderMap::get)
                .filter(order -> order.getDeliveryTime() > givenTime)
                .count();
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        // your code here
        // code should return string in format HH:MM
        if (!partnerToOrderMap.containsKey(partnerId)) return "00:00";

        int maxTime = partnerToOrderMap.get(partnerId).stream()
                .map(orderMap::get)
                .mapToInt(Order::getDeliveryTime)
                .max().orElse(0);

        return String.format("%02d:%02d", maxTime / 60, maxTime % 60);

    }
}