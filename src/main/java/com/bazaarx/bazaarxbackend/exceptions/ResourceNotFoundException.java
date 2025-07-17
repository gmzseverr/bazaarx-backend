package com.bazaarx.bazaarxbackend.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Eğer Spring ile kullanılıyorsa, @ResponseStatus ile HTTP durum kodu belirtebiliriz.
@ResponseStatus(HttpStatus.NOT_FOUND) // Bu exception fırlatıldığında 404 Not Found dönecek
public class ResourceNotFoundException extends RuntimeException { // ✨ RuntimeException'ı extend ediyoruz

    public ResourceNotFoundException(String message) {
        super(message); // Exception'ın yapıcı metodunu çağırarak mesajı iletiyoruz
    }

    // İsteğe bağlı: Hatanın kök nedenini de iletmek için bir constructor
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}