package com.nhom15.fashion.validator;


import com.nhom15.fashion.repositories.IUserRepository;
import com.nhom15.fashion.validator.annotation.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

    public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String>{
        @Autowired
        private IUserRepository userRepository;

        @Override
        public boolean isValid(String username, ConstraintValidatorContext context){
            if(userRepository == null)
                return true;
            return userRepository.findByUsername(username) == null;
        }
    }

