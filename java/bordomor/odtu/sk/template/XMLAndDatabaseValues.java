package bordomor.odtu.sk.template;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XMLAndDatabaseValues 
{
	String tableName() default "";
    String tagName() default "";
    String defaultVariable() default "";
}