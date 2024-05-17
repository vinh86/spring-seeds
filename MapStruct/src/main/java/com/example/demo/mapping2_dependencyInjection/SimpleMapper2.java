package com.example.demo.mapping2_dependencyInjection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class SimpleMapper2 {
    @Autowired protected SimpleService simpleService;
    
    @Mapping(target = "name", expression = "java(simpleService.getName(source.getName()))")
    public abstract SimpleDestination sourceToDestination(SimpleSource source);
    public abstract SimpleSource destinationToSource(SimpleDestination destination);
}
