package com.hemw.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * <pre>
 * 一定要在spring.xml中加上:
 * <code>&lt;bean id="serviceLocator" class="com.hmw.spring.ServiceLocator" singleton="true" /&gt;</code>
 * 
 * 如果不支持 singleton="true"， 则将其改为：scope="singleton"
 * 当对serviceLocator实例时就自动设置BeanFactory,以便后来可直接用beanFactory
 *</pre>
 * @author Carl He
 *
 */
public class ServiceLocator implements BeanFactoryAware {
	private static BeanFactory beanFactory = null;

	private static ServiceLocator servlocator = null;

	public void setBeanFactory(BeanFactory factory) throws BeansException {
		ServiceLocator.beanFactory = factory;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public static ServiceLocator getInstance() {
		if (servlocator == null)
			servlocator = (ServiceLocator) beanFactory.getBean("serviceLocator");
		return servlocator;
	}

	/**
	 * 根据提供的bean名称得到相应的服务类
	 * 
	 * @param servName
	 *            bean名称
	 */
	public static Object getService(String servName) {
		return beanFactory.getBean(servName);
	}

	/**
	 * 根据提供的bean名称得到对应于指定类型的服务类
	 * 
	 * @param servName
	 *            bean名称
	 * @param clazz
	 *            返回的bean类型,若类型不匹配,将抛出异常
	 */
	public static Object getService(String servName, Class<?> clazz) {
		return beanFactory.getBean(servName, clazz);
	}
}
