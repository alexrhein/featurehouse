package composer.methodToFeatureMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import composer.rules.rtcomp.java.JavaRuntimeFunctionRefinement;
import composer.rules.rtcomp.java.JavaRuntimeFunctionRefinement.Signature;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class MethodIdentifier {
	private boolean isConstructor = false;
	private String methodName = "";
	private String className = "";
	private String classPackage = "";
	private List<String> parameterTypes = new ArrayList<String>(8);
	private String returnType = "";
	private String originFeature = "";
	
	private static final Set<String> primitiveTypeNames = new HashSet<String>();
	{
		primitiveTypeNames.add("byte");
		primitiveTypeNames.add("short");
		primitiveTypeNames.add("int");
		primitiveTypeNames.add("long");
		primitiveTypeNames.add("float");
		primitiveTypeNames.add("double");
		primitiveTypeNames.add("boolean");
		primitiveTypeNames.add("char");
		primitiveTypeNames.add("void");
	}
	private static final Set<String> javaLangTypes = new HashSet<String>();
	{
		// downloaded from here: http://docs.oracle.com/javase/7/docs/api/java/lang/package-summary.html (13.05.2014)");
		// removed generics ("<T>")
		//Interfaces
		javaLangTypes.add("Appendable");
		javaLangTypes.add("AutoCloseable");
		javaLangTypes.add("CharSequence");
		javaLangTypes.add("Cloneable");
		javaLangTypes.add("Comparable");
		javaLangTypes.add("Iterable");
		javaLangTypes.add("Readable");
		javaLangTypes.add("Runnable");
		javaLangTypes.add("Thread.UncaughtExceptionHandler");

		//Classes
		javaLangTypes.add("Boolean");
		javaLangTypes.add("Byte");
		javaLangTypes.add("Character");
		javaLangTypes.add("Character.Subset");
		javaLangTypes.add("Character.UnicodeBlock");
		javaLangTypes.add("Class");
		javaLangTypes.add("ClassLoader");
		javaLangTypes.add("ClassValue");
		javaLangTypes.add("Compiler");
		javaLangTypes.add("Double");
		javaLangTypes.add("Enum");
		javaLangTypes.add("Float");
		javaLangTypes.add("InheritableThreadLocal");
		javaLangTypes.add("Integer");
		javaLangTypes.add("Long");
		javaLangTypes.add("Math");
		javaLangTypes.add("Number");
		javaLangTypes.add("Object");
		javaLangTypes.add("Package");
		javaLangTypes.add("Process");
		javaLangTypes.add("ProcessBuilder");
		javaLangTypes.add("ProcessBuilder.Redirect");
		javaLangTypes.add("Runtime");
		javaLangTypes.add("RuntimePermission");
		javaLangTypes.add("SecurityManager");
		javaLangTypes.add("Short");
		javaLangTypes.add("StackTraceElement");
		javaLangTypes.add("StrictMath");
		javaLangTypes.add("String");
		javaLangTypes.add("StringBuffer");
		javaLangTypes.add("StringBuilder");
		javaLangTypes.add("System");
		javaLangTypes.add("Thread");
		javaLangTypes.add("ThreadGroup");
		javaLangTypes.add("ThreadLocal");
		javaLangTypes.add("Throwable");
		javaLangTypes.add("Void");

		//Enums
		javaLangTypes.add("Character.UnicodeScript");
		javaLangTypes.add("ProcessBuilder.Redirect.Type");
		javaLangTypes.add("Thread.State");

		//Exceptions
		javaLangTypes.add("ArithmeticException");
		javaLangTypes.add("ArrayIndexOutOfBoundsException");
		javaLangTypes.add("ArrayStoreException");
		javaLangTypes.add("ClassCastException");
		javaLangTypes.add("ClassNotFoundException");
		javaLangTypes.add("CloneNotSupportedException");
		javaLangTypes.add("EnumConstantNotPresentException");
		javaLangTypes.add("Exception");
		javaLangTypes.add("IllegalAccessException");
		javaLangTypes.add("IllegalArgumentException");
		javaLangTypes.add("IllegalMonitorStateException");
		javaLangTypes.add("IllegalStateException");
		javaLangTypes.add("IllegalThreadStateException");
		javaLangTypes.add("IndexOutOfBoundsException");
		javaLangTypes.add("InstantiationException");
		javaLangTypes.add("InterruptedException");
		javaLangTypes.add("NegativeArraySizeException");
		javaLangTypes.add("NoSuchFieldException");
		javaLangTypes.add("NoSuchMethodException");
		javaLangTypes.add("NullPointerException");
		javaLangTypes.add("NumberFormatException");
		javaLangTypes.add("ReflectiveOperationException");
		javaLangTypes.add("RuntimeException");
		javaLangTypes.add("SecurityException");
		javaLangTypes.add("StringIndexOutOfBoundsException");
		javaLangTypes.add("TypeNotPresentException");
		javaLangTypes.add("UnsupportedOperationException");
		javaLangTypes.add("Error Summary  Error 	Description");
		javaLangTypes.add("AbstractMethodError");
		javaLangTypes.add("AssertionError");
		javaLangTypes.add("BootstrapMethodError");
		javaLangTypes.add("ClassCircularityError");
		javaLangTypes.add("ClassFormatError");
		javaLangTypes.add("Error");
		javaLangTypes.add("ExceptionInInitializerError");
		javaLangTypes.add("IllegalAccessError");
		javaLangTypes.add("IncompatibleClassChangeError");
		javaLangTypes.add("InstantiationError");
		javaLangTypes.add("InternalError");
		javaLangTypes.add("LinkageError");
		javaLangTypes.add("NoClassDefFoundError");
		javaLangTypes.add("NoSuchFieldError");
		javaLangTypes.add("NoSuchMethodError");
		javaLangTypes.add("OutOfMemoryError");
		javaLangTypes.add("StackOverflowError");
		javaLangTypes.add("ThreadDeath");
		javaLangTypes.add("UnknownError");
		javaLangTypes.add("UnsatisfiedLinkError");
		javaLangTypes.add("UnsupportedClassVersionError");
		javaLangTypes.add("VerifyError");
		javaLangTypes.add("VirtualMachineError");

		//Annotations
		javaLangTypes.add("Deprecated");
		javaLangTypes.add("Override");
		javaLangTypes.add("SafeVarargs");
		javaLangTypes.add("SuppressWarnings");
	}
	
	/**
	 * Assuming that the passed FSTTerminal is a "MethodDecl" or "ConstructorDecl" (getType()), 
	 * this method extracts information from the terminal to build a new MethodIdentifier for it.
	 */
	public MethodIdentifier(FSTTerminal functionNode, FSTNonTerminal classNode) {
		assert ("ConstructorDecl".equals(functionNode.getType()) || "MethodDecl".equals(functionNode.getType())) : 
			"Tried to get method signature of non-method/constructor FST node";
		if ("ConstructorDecl".equals(functionNode.getType()))
			isConstructor=true;
		originFeature = functionNode.getOriginalFeatureName();
		Signature sig = JavaRuntimeFunctionRefinement.Signature.fromString(functionNode.getBody());
		methodName = sig.name;

		// determine package name of the file containing this method; 
		// first try compilation unit of the parameter classNode; if that does not yield a package, try compilationUnit of the method
		// classNode might be an inner class, so we have to go up until we reach a CompilationUnit Node.
		
		FSTNonTerminal compilationUnit = (FSTNonTerminal)classNode.getParent(); // each class should be in a compilation unit and it should be a non terminal
		while (! "CompilationUnit".equals(compilationUnit.getType()) && compilationUnit.getParent()!=null) {
			compilationUnit = compilationUnit.getParent();
		}
		classPackage = getPackageNameFromCompilationUnit(compilationUnit);
		if (classPackage == null || classPackage.isEmpty()) {
			// try different compilation unit
			FSTNonTerminal functionCompilationUnit = getCompilationUnit(functionNode);
			if (functionCompilationUnit!=null && !functionCompilationUnit.equals(compilationUnit)) {
				classPackage = getPackageNameFromCompilationUnit(functionCompilationUnit);
			}
		}
		
		// collect imported type names using import statements from compilationUnit
		Map<String, String> importedTypes = collectImportedTypes(compilationUnit);
		// if compilation unit A of functionNode is not equal to parameter compilation unit B, collect import statements from A, too
		FSTNonTerminal functionCompilationUnit = getCompilationUnit(functionNode);
		if (functionCompilationUnit!=null && !functionCompilationUnit.equals(compilationUnit)) {
			importedTypes.putAll(collectImportedTypes(functionCompilationUnit));
		}
		
		// determine return Type and parameters; at this point imports must have been collected because they are used here (resolveImportedTypeName)
		returnType = sig.returnType.
				replaceAll("public","").replaceAll("private","").replaceAll("static","")
				.replaceAll("protected","").replaceAll("final","").replaceAll("synchronized","")
				.replaceAll("native","").replaceAll("abstract","").replaceAll("transient","")
				.replaceAll("strictfp","").trim();
		returnType = resolveImportedTypeName(returnType, classPackage, importedTypes);
		String paramList = sig.paramlist.trim();
		if (paramList.startsWith("(")) paramList=paramList.substring(1);
		if (paramList.endsWith(")")) paramList=paramList.substring(0, paramList.length()-1);
		//paramList is a comma-seperated list of all function parameters
		if (!paramList.trim().isEmpty()) {
			List<String> parameters = getElementsFromParamList(paramList);
			for (String param : parameters) {
				// param is something like "Bar b", remove the " b".
				String paramType = param.substring(0, param.lastIndexOf(" ")).trim();
				parameterTypes.add(resolveImportedTypeName(paramType, classPackage, importedTypes));
			}
		}
		className = classNode.getName();
	}
	/**
	 * Collects a map of all types imported in this compilation unit.
	 * Map<String,String>  key:= simpleTypeName (class name); value:= fully qualified type name (as stated in import)
	 */
	private static Map<String, String> collectImportedTypes(FSTNonTerminal compilationUnit) {
		Map<String, String> importedTypes = new HashMap<>();
		for (FSTNode child : compilationUnit.getChildren()) {
			if (child.getType().equals("ImportDeclaration")) {
				FSTTerminal importNode = (FSTTerminal) child;
				String importType = importNode.getBody().trim().replaceFirst("import ", "");
				importType = importType.substring(0, importType.length()-1); // remove trailing ";"
				if (importType.endsWith("*")) {
					System.err.println("Found unqualified import \""+importType+"\"in file " + compilationUnit.getName() + " feature " + compilationUnit.getFeatureName() + "\n\tcannot resolve types from this package.");
				} else if (!importType.contains(".")) {
					importedTypes.put(importType, importType);
				} else {
					String simpleTypeName = importType.substring(importType.lastIndexOf("."));
					importedTypes.put(simpleTypeName, importType);
				}
			}
		}
		return importedTypes;
	}
	/** Searches the passed compilation unit for a package declaration, returns null if none is found.
	 */
	private static String getPackageNameFromCompilationUnit(
			FSTNonTerminal compilationUnit) {
		String packageName = "";
		for (FSTNode node: compilationUnit.getChildren()) {
			if ("PackageDeclaration".equals(node.getType())) {
				packageName = ((FSTTerminal)node).getBody();
				if (packageName.startsWith("package "))
					packageName=packageName.substring("package ".length());
				if (packageName.endsWith(";"))
					packageName=packageName.substring(0, packageName.length()-1);
				break; // exit for
			}
		}
		return packageName;
	}
	/** Traverses parent nodes until a compilation unit is found; returns this compilation unit node
	 */
	private static FSTNonTerminal getCompilationUnit(FSTTerminal functionNode) {
		FSTNode ret = functionNode;
		while (!ret.getType().equals("CompilationUnit") && ret.getParent()!=null) {
			ret = ret.getParent();
		}
		if (ret.getType().equals("CompilationUnit") && ret instanceof FSTNonTerminal)
			return (FSTNonTerminal) ret;
		else return null;
	}
	/**
	 * Tries to resolve a type name to its fully qualified name using information from the collected import statements and the current package declaration.
	 */
	private static String resolveImportedTypeName(String paramType, String classPackage,
			Map<String, String> importedTypes) {
		// if endswith "[]" resolve recursively
		// if has generics ("List<String>") resolve recursively
		// else if has "implements" or "extends" resolve recursively
		// else if paramType is a primitiveType, return it
		// else if paramType is imported, return the imported type name
		// else if paramType is from java lang (standardApiTypes) return the paramType unchanged
		// else assume that paramType is from current package and prepend currentPackage name
		if (paramType.endsWith("[]")) {
			String innerType = paramType.substring(0, paramType.length()-2).trim();
			return resolveImportedTypeName(innerType, classPackage, importedTypes) + "[]";
		} else if (paramType.contains("<")) {
			// example: "Foo<Bar,Bla<T extends Throwable>>"
			// Foo, Bar, Bla and Throwable need to be resolved
			int genStart = paramType.indexOf("<");
			String outerType = resolveImportedTypeName(paramType.substring(0,genStart).trim(), classPackage, importedTypes);
			String typeParamList = paramType.substring(genStart+1, paramType.length()-1).trim(); // remove last ">"
			ArrayList<String> typeParams = new ArrayList<String>();
			for (String p : getElementsFromParamList(typeParamList)) {
				typeParams.add(resolveImportedTypeName(p, classPackage, importedTypes));
			}
			String ret ="";
			for (String tp : typeParams) {
				if (ret.isEmpty())
					ret = tp;
				else
					ret = ", " + tp;
			}
			return outerType + "<" + ret + ">";
		} else if (paramType.contains(" implements ")) {
			int begin = paramType.indexOf(" implements ");
			String part1 = resolveImportedTypeName(paramType.substring(0, begin).trim(), classPackage, importedTypes);
			String part2 = resolveImportedTypeName(paramType.substring(begin+ " implements ".length()).trim(), classPackage, importedTypes);
			return part1 + " implements " + part2;
		} else if (paramType.contains(" extends ")) {
			int begin = paramType.indexOf(" extends ");
			String part1 = resolveImportedTypeName(paramType.substring(0, begin).trim(), classPackage, importedTypes);
			String part2 = resolveImportedTypeName(paramType.substring(begin+ " extends ".length()).trim(), classPackage, importedTypes);
			return part1 + " extends " + part2;
		} else if (primitiveTypeNames.contains(paramType)) {
			return paramType;
		} if (importedTypes.containsKey(paramType)) {
			return importedTypes.get(paramType);
		} else if (javaLangTypes.contains(paramType)) {
			return "java.lang." + paramType;
		} else if (classPackage.isEmpty()) {
			// default package
			return paramType;
		} else {
			return classPackage + "." + paramType;
		}
	}
	/**
	 * Returns the top level elements of a parameter list.
	 * It does not matter whether the list was a type parameter list (enclosed by "<" ">" in a method signature) or not.
	 * All elements of the returned list are trimmed.
	 * 
	 * Given the String "Bar,Bla<T extends Throwable>" 
	 * from type "Foo<Bar,Bla<T extends Throwable>>", 
	 * this method returns a List { "Bar" , "Bla<T extends Throwable>" }.
	 * (The type parameters of Bla must be resolved subsequently)
	 * 
	 * Given the String "Bar a,Bla<T extends Throwable> b" from parameter list "method(Bar a,Bla<T extends Throwable> b)",
	 * this method returns a List { "Bar a" , "Bla<T extends Throwable> b" }.
	 * (The type parameters of Bla must be resolved subsequently)
	 */
	private static List<String> getElementsFromParamList(String paramList) {
		int openBrackets = 0; // track number of open brackets and split type parameter from string when openBrackets is 0 and symbol is ','
		ArrayList<String> typeParams = new ArrayList<String>();
		int segmentStart = 0;
		for (int i = 0; i < paramList.length(); i++) {
			switch (paramList.charAt(i)) {
			case '<': openBrackets++; break;
			case '>': openBrackets--; break;
			case ',':
				if (openBrackets==0) {
					String tParam = paramList.substring(segmentStart, i).trim();
					typeParams.add(tParam);
				}
				segmentStart = i+1;
				break;
			}
		}
		// last segment
		String tParam = paramList.substring(segmentStart).trim();
		typeParams.add(tParam);
		return typeParams;
	}
	private MethodIdentifier() {}
	public void setIsConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public List<String> getParameterTypes() {
		return parameterTypes;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public void setOriginFeature(String originFeature) {
		this.originFeature = originFeature;
	}
	@Override
	public String toString() {
		String parameters = "";
		boolean first = true;
		for (String s : parameterTypes) {
			if (first) {
				parameters = s;
				first = false;
			} else
				parameters = parameters + ";" + s;
		}
		if (isConstructor)
			return (classPackage.isEmpty()?"":classPackage+".") + className + "(" + parameters + ")" + "=" + originFeature;
		else
			return (classPackage.isEmpty()?"":classPackage+".") + className + "." + methodName + "(" + parameters + ")" + returnType + "=" + originFeature;
	}
	public MethodIdentifier deepClone() {
		MethodIdentifier mi = new MethodIdentifier();
		mi.isConstructor = isConstructor;
		mi.methodName = methodName;
		mi.className = className;
		mi.classPackage = classPackage;
		mi.parameterTypes = new ArrayList<String>(parameterTypes);
		mi.returnType = returnType;
		mi.originFeature = originFeature;
		return mi;
	}
	
	/**
	 * Map of primitive Java types to their JNI identifiers; used in the JNI export function
	 */
	private static final Map<String,String> primitiveTypeNameJNImap = new HashMap<String,String>();
	{
		primitiveTypeNameJNImap.put("byte", "B");
		primitiveTypeNameJNImap.put("short", "S");
		primitiveTypeNameJNImap.put("int", "I");
		primitiveTypeNameJNImap.put("long", "J");
		primitiveTypeNameJNImap.put("float", "F");
		primitiveTypeNameJNImap.put("double", "D");
		primitiveTypeNameJNImap.put("boolean", "Z");
		primitiveTypeNameJNImap.put("char", "C");
		primitiveTypeNameJNImap.put("void", "V");
	}
	/**
	 * Java Native Method Signature
	 * http://journals.ecs.soton.ac.uk/java/tutorial/native1.1/implementing/method.html
	 * E.g.
	 * package de
	 * class me {int foo(String) {..}} 
	 * de.me.foo(Ljava/lang/String;)I;
	 */
	public String toJavaNativeSignatureWithFeature() {
		String prefix =
				(this.classPackage.isEmpty()?"":this.classPackage + ".") +
				this.className + "." +
				(this.isConstructor?"<init>":this.methodName);
		String params = "";
		for (String s : this.parameterTypes) {
			params = params + transformTypeToJNI(s);
		}
		if (isConstructor)
			return prefix + "(" + params + ")" + "=" + this.originFeature;
		else {
			String returnType = transformTypeToJNI(this.returnType);
			return prefix + "(" + params + ")" + returnType + "=" + this.originFeature;
		}
	}
	private String transformTypeToJNI(String type) {
		String ret = "";
		String p = type;
		// ignore generics/type parameters; they are excluded from java native signature
		if (type.contains("<")) p = type.substring(0, type.indexOf("<"));
		if (p.contains("[")) {
			//Array types are indicated by a leading square bracket ([) followed by the type of the array elements.
			p = p.replaceAll("\\[\\s*\\]", "");
			ret=ret+"[";
		}
		if (primitiveTypeNameJNImap.containsKey(p)) {
			ret = ret + primitiveTypeNameJNImap.get(p); // no semicolon after primitive types
		} else {
			ret = ret + "L" + p.replaceAll("\\.", "/") + ";";
		}
		return ret;
	}
}
