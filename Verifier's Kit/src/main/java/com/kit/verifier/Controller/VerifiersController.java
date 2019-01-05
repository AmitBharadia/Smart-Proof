package com.kit.verifier.Controller;

import com.kit.verifier.zeroknowledge.prover.Verifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class VerifiersController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path="/ping")
    public ResponseEntity<String> ping() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("MyResponseHeader", "MyValue");
        return new ResponseEntity<String>("Zkp Verifier's Kit is available ", responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/verifier")
    public ResponseEntity<String> index(@RequestBody InputBody inputBody) {

        String uid = inputBody.uid;
        String lowerBound = inputBody.lowerBound;
        String upperBound = inputBody.upperBound;

        System.out.println("Unique_doc_id is: "+uid);

        Iterable<Zkp_user> zkp_users = userRepository.findAll();

        Zkp_user zkp_user = null;
        for (Zkp_user p: zkp_users) {
            if(uid.equalsIgnoreCase(p.getUnique_doc_id())
                    && p.getType().equalsIgnoreCase("Prover")) {
                zkp_user = p;
                break;
            }
        }

        if (zkp_user==null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            return new ResponseEntity<String>("Error", responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String fileName = zkp_user.getDoc_name() + ".data";

        Verifier verifier = new Verifier();
        if (verifier.runValidation(lowerBound, upperBound, fileName)) {
            System.out.println("Generated the proof successfully!!");
            HttpHeaders responseHeaders = new HttpHeaders();
            return new ResponseEntity<String>("Passed the verification", responseHeaders, HttpStatus.OK);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>("Error", responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
