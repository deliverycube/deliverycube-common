/**
 * 
 */
package deliverycube.common.atg.transaction.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Make sure aspectjweaver-1.6.11.jar is provided with javaagent 

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ATGTransaction {

    PropagationLevel propagationLevel() default PropagationLevel.REQUIRED;

}
