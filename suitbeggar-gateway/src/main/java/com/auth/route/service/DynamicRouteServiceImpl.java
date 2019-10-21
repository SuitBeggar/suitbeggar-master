package com.auth.route.service;

import com.auth.route.vo.GatewayFilterDefinition;
import com.auth.route.vo.GatewayPredicateDefinition;
import com.auth.route.vo.GatewayRouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fangyitao on 2019/8/6.
 * 动态路由
 */
@Service
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }


    //刷新路由
    public void refresh(){
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }
    //从数据库获取路由信息
    public String save() {

        //从数据库拿到路由配置
        List<GatewayRouteDefinition> gatewayRouteList = new ArrayList<GatewayRouteDefinition>(); //= gatewayRouteBiz.selectListAll();

        gatewayRouteList.forEach(gatewayRoute -> {
            RouteDefinition definition = new RouteDefinition();
            Map<String, String> predicateParams = new HashMap<>(8);

            //设置断言
            List<PredicateDefinition> pdList=new ArrayList<>();
            List<GatewayPredicateDefinition> gatewayPredicateDefinitionList=gatewayRoute.getPredicates();
            for (GatewayPredicateDefinition gpDefinition: gatewayPredicateDefinitionList) {
                PredicateDefinition predicate = new PredicateDefinition();
                predicate.setArgs(gpDefinition.getArgs());
                predicate.setName(gpDefinition.getName());
                pdList.add(predicate);
            }

            definition.setPredicates(pdList);

            //设置过滤器
            List<FilterDefinition> filters = new ArrayList();
            List<GatewayFilterDefinition> gatewayFilters = gatewayRoute.getFilters();
            for(GatewayFilterDefinition filterDefinition : gatewayFilters){
                FilterDefinition filter = new FilterDefinition();
                filter.setName(filterDefinition.getName());
                filter.setArgs(filterDefinition.getArgs());
                filters.add(filter);
            }
            definition.setFilters(filters);

            URI uri = null;
            if(gatewayRoute.getUri().startsWith("http")){
                uri = UriComponentsBuilder.fromHttpUrl(gatewayRoute.getUri()).build().toUri();
            }else{
                // uri为 lb://consumer-service 时使用下面的方法
                uri = URI.create(gatewayRoute.getUri());
            }
            definition.setUri(uri);

            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        });

        this.publisher.publishEvent(new RefreshRoutesEvent(this));

        return "success";


    }

    //增加路由
    public String add(RouteDefinition definition) {
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }

    //更新路由
    public String update(RouteDefinition definition) {
        try {
            delete(definition.getId());
        } catch (Exception e) {
            return "update fail,not find route  routeId: "+definition.getId();
        }
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            return "update route  fail";
        }
    }
    //删除路由
    public Mono<ResponseEntity<Object>> delete(String id) {
        return this.routeDefinitionWriter.delete(Mono.just(id)).then(Mono.defer(() -> {
            return Mono.just(ResponseEntity.ok().build());
        })).onErrorResume((t) -> {
            return t instanceof NotFoundException;
        }, (t) -> {
            return Mono.just(ResponseEntity.notFound().build());
        });
    }
}
