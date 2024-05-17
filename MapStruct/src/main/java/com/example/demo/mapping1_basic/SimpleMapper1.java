package com.example.demo.mapping1_basic;

import org.mapstruct.Mapper;

@Mapper
public interface SimpleMapper1 {
    SimpleDestination sourceToDestination(SimpleSource source);
    SimpleSource destinationToSource(SimpleDestination destination);
}
