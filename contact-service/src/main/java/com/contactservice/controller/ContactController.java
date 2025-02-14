package com.contactservice.controller;

import com.contactservice.dto.ContactsRequestDTO;
import com.contactservice.dto.Error;
import com.contactservice.dto.GroupDTO;
import com.contactservice.dto.GroupRequestDTO;
import com.contactservice.models.Contact;
import com.contactservice.models.Group;
import com.contactservice.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.Location;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
public class ContactController {
    @Autowired
    private ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/api/contact")
    public ResponseEntity<Error> createContact(@RequestBody Contact contact) throws URISyntaxException {
        try {
            String id = contactService.addContact(contact);
            String location = "/api/contact/" + id;
            return ResponseEntity.created(new URI(location)).build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(Error.builder().message(e.getMessage()).errorCode("contact-service.100").build());
        }

    }

    @GetMapping("/api/contact")
    public List<Contact> getContacts(@RequestBody ContactsRequestDTO contactsRequestDTO){
        return contactService.getContacts(contactsRequestDTO.getUserId());
    }

    @PostMapping("/api/contact/groups")
    public ResponseEntity createGroup(@RequestBody GroupDTO groupDTO) throws URISyntaxException {
        String groupId= contactService.createGroup(groupDTO);
        String location="/api/contact/groups/"+groupId;
        return ResponseEntity.created(new URI(location)).build();
    }

    @GetMapping("/api/contact/groups")
    public ResponseEntity<List<GroupDTO>> getContactGroups(@RequestBody GroupRequestDTO groupRequestDTO) {
        List<GroupDTO> contactGroups = contactService.readGroups(groupRequestDTO.getUserId());
        return ResponseEntity.ok(contactGroups);
    }

    @GetMapping("/api/contact/groups/{groupId}")
    public ResponseEntity<GroupDTO> getContactGroup(@PathVariable String groupId ) {
        Optional<GroupDTO> contactGroup = contactService.readGroup(groupId);
        System.out.println(contactGroup.get());
        return ResponseEntity.ok(contactGroup.get());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Error> returnErrors(RuntimeException exception){
        if(exception.getMessage().contains("404")){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.badRequest().body(Error.builder().message(exception.getMessage()).build());
        }
    }

    @PutMapping("/api/contact/groups/{groupId}")
    public ResponseEntity updateGroup(@RequestBody GroupDTO groupDTO, @PathVariable String groupId ){
        contactService.updateGroup(groupDTO,groupId);
        return ResponseEntity.noContent().build();
    }

}
