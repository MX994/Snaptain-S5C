package com.sun.jna.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReflectionUtils {
  private static Constructor CONSTRUCTOR_LOOKUP_CLASS;
  
  private static final Logger LOG = Logger.getLogger(ReflectionUtils.class.getName());
  
  private static final Method METHOD_HANDLES_BIND_TO;
  
  private static final Method METHOD_HANDLES_INVOKE_WITH_ARGUMENTS;
  
  private static final Method METHOD_HANDLES_LOOKUP;
  
  private static final Method METHOD_HANDLES_LOOKUP_FIND_SPECIAL;
  
  private static final Method METHOD_HANDLES_LOOKUP_IN;
  
  private static final Method METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL;
  
  private static final Method METHOD_HANDLES_PRIVATE_LOOKUP_IN;
  
  private static final Method METHOD_IS_DEFAULT = lookupMethod(Method.class, "isDefault", new Class[0]);
  
  private static final Method METHOD_TYPE;
  
  static {
    METHOD_HANDLES_LOOKUP = lookupMethod(clazz1, "lookup", new Class[0]);
    METHOD_HANDLES_LOOKUP_IN = lookupMethod(clazz3, "in", new Class[] { Class.class });
    METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL = lookupMethod(clazz3, "unreflectSpecial", new Class[] { Method.class, Class.class });
    METHOD_HANDLES_LOOKUP_FIND_SPECIAL = lookupMethod(clazz3, "findSpecial", new Class[] { Class.class, String.class, clazz4, Class.class });
    METHOD_HANDLES_BIND_TO = lookupMethod(clazz2, "bindTo", new Class[] { Object.class });
    METHOD_HANDLES_INVOKE_WITH_ARGUMENTS = lookupMethod(clazz2, "invokeWithArguments", new Class[] { Object[].class });
    METHOD_HANDLES_PRIVATE_LOOKUP_IN = lookupMethod(clazz1, "privateLookupIn", new Class[] { Class.class, clazz3 });
    METHOD_TYPE = lookupMethod(clazz4, "methodType", new Class[] { Class.class, Class[].class });
  }
  
  private static Object createLookup() throws Exception {
    return METHOD_HANDLES_LOOKUP.invoke(null, new Object[0]);
  }
  
  private static Object createPrivateLookupIn(Class paramClass, Object paramObject) throws Exception {
    return METHOD_HANDLES_PRIVATE_LOOKUP_IN.invoke(null, new Object[] { paramClass, paramObject });
  }
  
  private static Constructor getConstructorLookupClass() {
    if (CONSTRUCTOR_LOOKUP_CLASS == null)
      CONSTRUCTOR_LOOKUP_CLASS = lookupDeclaredConstructor(lookupClass("java.lang.invoke.MethodHandles$Lookup"), new Class[] { Class.class }); 
    return CONSTRUCTOR_LOOKUP_CLASS;
  }
  
  public static Object getMethodHandle(Method paramMethod) throws Exception {
    Object object = createLookup();
    try {
      return mhViaFindSpecial(createPrivateLookupIn(paramMethod.getDeclaringClass(), object), paramMethod);
    } catch (Exception exception) {
      return mhViaUnreflectSpecial(getConstructorLookupClass().newInstance(new Object[] { paramMethod.getDeclaringClass() }, ), paramMethod);
    } 
  }
  
  public static Object invokeDefaultMethod(Object paramObject1, Object paramObject2, Object... paramVarArgs) throws Throwable {
    paramObject1 = METHOD_HANDLES_BIND_TO.invoke(paramObject2, new Object[] { paramObject1 });
    return METHOD_HANDLES_INVOKE_WITH_ARGUMENTS.invoke(paramObject1, new Object[] { paramVarArgs });
  }
  
  public static boolean isDefault(Method paramMethod) {
    Method method = METHOD_IS_DEFAULT;
    if (method == null)
      return false; 
    try {
      return ((Boolean)method.invoke(paramMethod, new Object[0])).booleanValue();
    } catch (IllegalAccessException illegalAccessException) {
      throw new RuntimeException(illegalAccessException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new RuntimeException(illegalArgumentException);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getCause();
      if (!(throwable instanceof RuntimeException)) {
        if (throwable instanceof Error)
          throw (Error)throwable; 
        throw new RuntimeException(throwable);
      } 
      throw (RuntimeException)throwable;
    } 
  }
  
  private static Class lookupClass(String paramString) {
    try {
      return Class.forName(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      Logger logger = LOG;
      Level level = Level.FINE;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Failed to lookup class: ");
      stringBuilder.append(paramString);
      logger.log(level, stringBuilder.toString(), classNotFoundException);
      return null;
    } 
  }
  
  private static Constructor lookupDeclaredConstructor(Class paramClass, Class... paramVarArgs) {
    if (paramClass == null) {
      LOG.log(Level.FINE, "Failed to lookup method: <init>#{1}({2})", new Object[] { paramClass, Arrays.toString((Object[])paramVarArgs) });
      return null;
    } 
    try {
      Constructor constructor = paramClass.getDeclaredConstructor(paramVarArgs);
      constructor.setAccessible(true);
      return constructor;
    } catch (Exception exception) {
      LOG.log(Level.FINE, "Failed to lookup method: <init>#{1}({2})", new Object[] { paramClass, Arrays.toString((Object[])paramVarArgs) });
      return null;
    } 
  }
  
  private static Method lookupMethod(Class paramClass, String paramString, Class... paramVarArgs) {
    if (paramClass == null) {
      LOG.log(Level.FINE, "Failed to lookup method: {0}#{1}({2})", new Object[] { paramClass, paramString, Arrays.toString((Object[])paramVarArgs) });
      return null;
    } 
    try {
      return paramClass.getMethod(paramString, paramVarArgs);
    } catch (Exception exception) {
      LOG.log(Level.FINE, "Failed to lookup method: {0}#{1}({2})", new Object[] { paramClass, paramString, Arrays.toString((Object[])paramVarArgs) });
      return null;
    } 
  }
  
  private static Object mhViaFindSpecial(Object paramObject, Method paramMethod) throws Exception {
    return METHOD_HANDLES_LOOKUP_FIND_SPECIAL.invoke(paramObject, new Object[] { paramMethod.getDeclaringClass(), paramMethod.getName(), METHOD_TYPE.invoke(null, new Object[] { paramMethod.getReturnType(), paramMethod.getParameterTypes() }), paramMethod.getDeclaringClass() });
  }
  
  private static Object mhViaUnreflectSpecial(Object paramObject, Method paramMethod) throws Exception {
    paramObject = METHOD_HANDLES_LOOKUP_IN.invoke(paramObject, new Object[] { paramMethod.getDeclaringClass() });
    return METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL.invoke(paramObject, new Object[] { paramMethod, paramMethod.getDeclaringClass() });
  }
  
  static {
    Class clazz1 = lookupClass("java.lang.invoke.MethodHandles");
    Class clazz2 = lookupClass("java.lang.invoke.MethodHandle");
    Class clazz3 = lookupClass("java.lang.invoke.MethodHandles$Lookup");
    Class clazz4 = lookupClass("java.lang.invoke.MethodType");
  }
}


/* Location:              /home/platinum/Documents/AndroidRE/com.guanxu.technology.snaptain_era_s5c_29_apps.evozi.com-dex2jar.jar!/com/sun/jna/internal/ReflectionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */