package com.userservice.service;

import com.userservice.models.Token;
import com.userservice.models.User;
import com.userservice.repository.TokenRepository;
import com.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private UserRepository userRepository;
    private TokenRepository tokenRepository;

    public String login(User user) {
        Optional<User> existingUser = userRepository.findById(user.getEmailId());

        if(existingUser.isPresent()){
           if(existingUser.get().getPassword().equals(user.getPassword())){
               UUID uuid=UUID.randomUUID();
               Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

               // Add 1 hour to the current timestamp
               Calendar calendar = Calendar.getInstance();
               calendar.setTimeInMillis(currentTimestamp.getTime());
               calendar.add(Calendar.HOUR, 1);
               Timestamp timestampPlusOneHour = new Timestamp(calendar.getTimeInMillis());

               Token token= Token.builder().user(user).token(uuid.toString()).timestamp(timestampPlusOneHour).build();

               tokenRepository.save(token);
               return uuid.toString();

           }else{
               throw new RuntimeException("Password incorrect");
           }
        }else{
            throw new RuntimeException("User not found, try Signing in ");
        }
    }

    public void signUp(User user) throws Exception{
        /* if user with same credentials exist we will throw an error
        else create new record in the database and return success response to user
        */
      Optional<User> existingUser = userRepository.findById(user.getEmailId());

      if(existingUser.isPresent()){
          throw new RuntimeException("User with this EmailId already Exists");
      }else{
          userRepository.save(user);
      }
    }
}
