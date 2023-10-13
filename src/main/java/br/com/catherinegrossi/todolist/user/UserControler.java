package br.com.catherinegrossi.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * modificador
 * public
 * private
 * protected
 */

 @RestController
 @RequestMapping("/users")
public class UserControler {

    @Autowired
    private IUserRepository userRepository;

    /**
     * string
     * interger (int)
     * double (casas decimais)
     * float (numero de caracteres)
     * char (A  c)
     * date
     * void (sem retorno do metodo)
     */
    @PostMapping("/")
    public ResponseEntity create (@RequestBody UserModel userModel) {

        var user = this.userRepository.findByUsername(userModel.getUsername());
       
        if(user != null){
            System.out.println("usuario ja existe");
            //mensagem de erro
            //status code
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("usuario ja existe");
        }

        var passwordHashred = BCrypt.withDefaults()
        .hashToString(12, userModel.getPassword().toCharArray());

        userModel.setPassword(passwordHashred);

        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.OK).body(userCreated);
    }
}
