package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


/**
 * @author Admin
 */
@Target(ElementType.TYPE)
public @interface Destination {
    /**
     * the name of the page in the route
     *
     */
    String pageUrl();

    /**
     * whether as the first startup surface in the route
     *
     */
    boolean asStarter() default false;

}
