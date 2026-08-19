# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep SignatureValidator class and methods
-keep class com.example.signaturelib.SignatureValidator {
    public *;
}

# Keep JNI method signatures
-keepclassmembers class com.example.signaturelib.SignatureValidator {
    private native java.lang.String nativeGetSignatureHash(android.content.Context);
    private native boolean nativeVerifySignature(android.content.Context, java.lang.String);
}
