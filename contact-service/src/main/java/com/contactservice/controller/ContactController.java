package com.contactservice.controller;

import com.contactservice.dto.ContactsRequestDTO;
import com.contactservice.dto.GroupDTO;
import com.contactservice.models.Contact;
import com.contactservice.models.Group;
import com.contactservice.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.Location;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class ContactController {
    private ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/api/contact")
    public ResponseEntity<String> createContact(@RequestBody Contact contact) throws URISyntaxException {
        String id=contactService.addContact(contact);
        String location="/api/contact/"+id;
        return ResponseEntity.created(new URI(location)).build();

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


    @PutMapping("/api/contact/groups/{groupId}")
    public ResponseEntity updateGroup(@RequestBody GroupDTO groupDTO, @PathVariable String groupId ){
        contactService.updateGroup(groupDTO,groupId);
        return ResponseEntity.noContent().build();
    }

}
