
package com.bazaarx.bazaarxbackend.mapper;

import com.bazaarx.bazaarxbackend.dto.RegisterRequest;
import com.bazaarx.bazaarxbackend.dto.UserResponseDTO;
import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserResponseDTO UserToUserResponseDTO(ApplicationUser user) {
        if (user == null) {
            return null;
        }

        return modelMapper.map(user, UserResponseDTO.class);
    }

    public ApplicationUser toApplicationUser(RegisterRequest registerDto) {
        if (registerDto == null) {
            return null;
        }
        ApplicationUser user = modelMapper.map(registerDto, ApplicationUser.class);
        user.setPassword(null);
        return user;
    }

}