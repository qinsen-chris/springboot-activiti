package com.gclfax.modules.activiti.service;

/**
 * Create by qs on ${date}
 */
public interface IBaseProcessService<T> {
    T queryObjectByBusinessKey(Class T,Object id);
}
