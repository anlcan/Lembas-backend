## Lembas-Backend		
Lembas-Backend is the service development part of the Lembas project. 

[Lembas-core](https://github.com/anlcan/Lembas-core) allows you to define data types(artifacts) and service interfaces(endpoints), Lembas-Backend on the other hand helps you generate description file based on your artifacts and enpoints, generate source code to be used to eaisly build mobile clients that communicates with the backend.

* [Getting started]()
* * [Maven depencies](#Maven depencies)
* * [CodeGenerator](#CodeGenerator)
* * [Dispatcher](#Dispatcher)
* * [Configuration](#Configuration)
* [Hello World](#Hello World)
* * [Endpoints](#Endpoints)
* * [Artifacts](#Artifacts)
* [Notes](#Notes)



### Getting started

#### Maven depencies
<a name="Maven depencies"></a>
Add maven dependencies to your project

```xml
	<dependency>
          <groupId>com.happyblueduck.lembas</groupId>
          <artifactId>lembas-core</artifactId>
          <version>2.1</version>
    </dependency>
	<dependency>
          <groupId>com.happyblueduck.lembas</groupId>
          <artifactId>lembas-backend</artifactId>
          <version>2.1</version>
	</dependency>
```

Lembas needs two servlet entry, one for generating source code and another one for processing and serving generated calls from source code:

#### CodeGenerator
<a name="CodeGenerator"></a>
Add code generation servlet *com.happyblueduck.lembas.servlets.CodeGen* to your *web.xml*. It is responsable from generating a description file of your project according to [Configuration]() , sending that description file to code generator and zipping the result. 

```xml
	<servlet>
        <servlet-name>Codegen</servlet-name>
        <servlet-class>com.happyblueduck.lembas.servlets.CodeGen</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>Codegen</servlet-name>
        <url-pattern>/lembas</url-pattern>
    </servlet-mapping>
```

#### Dispatcher
<a name="Dispatcher"></a>
For serving LembasRequests, you need to extend *com.happyblueduck.lembas.servlets.Dispatcher* (thoght you can also directly use it, extending will give you more control) . Create a new java file named *MobileDispatcher* :
	
```java
	package com.happyblueduck.application; 
	
	import com.happyblueduck.lembas.servlets.Dispatcher;
	import javax.servlet.ServletException;
	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;
	import java.io.IOException;

	public class MobileDispatcher extends Dispatcher {

	    @Override
	    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
	        // for now, our dispatcher only calls the original implementation..
	        // ..and time the execution
	    	double start =     System.currentTimeMillis(); 
            super.doPost(req, res);
            double duration = System.currentTimeMillis() - start; 
            System.out.println("LembasRequest executed in :" + duration + "ms");
	    }
	
	    @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	        resp.getWriter().println("Hello you have reached the winter of our discontent");
	    }
	}

```

You can now add your dispatcher to *web.xml*

```xml
    <servlet>
        <servlet-name>Dispatcher</servlet-name>
        <servlet-class>com.happyblueduck.application.MobileDispatcher</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>Dispatcher</servlet-name>
        <url-pattern>/Arwen/*</url-pattern>
    </servlet-mapping>
```
#### Configuration
<a name="Configuration"></a>
You can now configure your *MobileDispatcher* by overriding [init()](http://docs.oracle.com/javaee/6/api/javax/servlet/GenericServlet.html#init%28%29) method

```java
 	@Override
 	public void init() throws ServletException {
		//service name and servlet url-mapping should match
        Config.serviceName  = "Arwen"; 
        Config.version      = "1.0";
        Config.HOST_URL     = http://localhost";
        Config.HOST_PORT    = "8080"
    }
```

If you start your server and go to [http://localhost:8080/lembas?target=objc](http://localhost:8080/lembas?target=objc), you should be able to download zipped source files iOS, which you can use with [Lembas-ios](https://github.com/anlcan/Lembas-ios). 

### Hello World

#### Endpoints
<a name="Endpoints"></a>
If you followed the steps and generated the source code, you notice that it only contains endpoint address and a base class.
You need artifacts and enpoints to be able to generate code and make request to your backend. 

Create a package in your project *com.happyblueduck.enpoints* and add your first endpoint by extending LembasRequest...

```java

	package com.happyblueduck.enpoints;

	import com.happyblueduck.lembas.core.LembasResponse;
	import com.happyblueduck.lembas.core.UtilSerializeException;
	import com.happyblueduck.lembas.processing.LembasActionContext;
	import com.happyblueduck.lembas.processing.LembasProcessRequest;
	import com.happyblueduck.lembas.processing.RequestProcessException;
	
	public String requestParamString;
	public int requestParamInt; 
	public double requestParamDouble;  
	
	public class HelloWorldRequest extends LembasProcessRequest {
	    @Override
	    public LembasResponse process(LembasActionContext lembasActionContext) throws RequestProcessException, UtilSerializeException {
	        HelloWorldResponse response = new HelloWorldResponse(); 
	        response.message = "Goodbye, cruel world";
	        
	        return response; 
	    }
	} 
```

...and LembasResponse

```java
	package com.happyblueduck.enpoints;

	import com.happyblueduck.lembas.core.LembasResponse;

	public class HelloWorldResponse extends LembasResponse {
		public String message; 
	}
```
	
#### Artifacts
<a name="Artifacts"></a>
We can also add an artifact to our backend. Create a package named *com.happyblueduck.artifact* and add an artifact by extending LembasObject:


```java
	package com.happyblueduck.artifacts;
	
	import com.happyblueduck.lembas.core.LembasObject
	import java.util.Date;
	import java.util.ArrayList; 
	
	public class HelloArtifact extends LembasObject{
		
		// lembas supports primiteves
		public String sstring; 
		public Double ddouble; 
		public Long   llong; 
		public Date ddate;
		
		// and arrays
		public ArrayList<String> aarray; 
		
		// and array of lembasObjects
		public ArrayList<HelloArtifact> artifacst; 
		
	}
```

Now that we have our first endpoint, we should update our Configuration:
	

```java
 	@Override
 	public void init() throws ServletException {
		//service name and servlet url-mapping should match
        Config.serviceName  = "Arwen"; 
        Config.version      = "1.0";
        Config.HOST_URL     = http://localhost";
        Config.HOST_PORT    = "8080"
        
        // endpoints and artifacts are added by packages
        Config.addEndpoint("com.happyblueduck.enpoints"); 
    }
```

If you generate the iOS source code now from [http://localhost:8080/lembas?target=objc](http://localhost:8080/lembas?target=objc), you will see HelloRequest, HelloResponse and HelloArtifact classes. You can now make service calls using [Lembas-ios](https://github.com/anlcan/Lembas-ios).

### Notes
<a name="Notes"></a>

- in order to create endpoint XXX you must create XXXRequest and XXXResponse respectively
- private fields and fields name which starts with "_" are excluded from generated source codes