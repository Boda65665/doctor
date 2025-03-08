package com.kafka1.demo.Converters;

import com.kafka1.demo.DTO.UserDTO;
import com.kafka1.demo.Entity.User;

public class UserConverter {

    public UserDTO userToUserDTO(User user){
        UserDTO convertedUser = new UserDTO();
        convertedUser.setId(user.getId());
        convertedUser.setPassword(user.getPassword());
        convertedUser.setEmail(user.getEmail());
        convertedUser.setLastName(user.getLastName());
        convertedUser.setFirstName(user.getFirstName());
        convertedUser.setRole(user.getRole());
        return convertedUser;
    }
}
