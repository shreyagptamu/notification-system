import { Box, Button, Input, Text, useToast } from "@chakra-ui/react";
import { useState } from "react";

class User{
  name;
  emailId;
  password;
}

function SignUp(){
    const[user, setUser]=useState(new User());
    let toast=useToast();
    function handleUserInput(event){
    user[event.target.id]=event.target.value;
    setUser(user);
    console.log(user);
    }
    async function signUpUser(){
      let response =  await fetch('http://localhost:8080/api/signUp', {
            method:'POST',
            headers:{
                'Content-Type':'application/json'
            },
            body:JSON.stringify(user)
        })
       if(response.status!==200){
        toast({
            title: 'Error signing up User.',
            status: 'error',
            isClosable: true,
            position: 'top-right'
        });
       }else{
        toast({
            title: 'User Signed up successfully',
            status: 'success',
            isClosable: true,
            position: 'top-right'
        });
       }
    }
    return(
    <Box display={'flex'} gap={'32px'} flexDirection={'column'} height={'380px'} width={'429px'} padding={'30px 49px 30px 49px'} background={'#F6F8FD'}  borderRadius={'8px'}>
    <Text fontSize={'1xl'} >Create account</Text>
    <div style={{
        display:"flex", 
        flexDirection:"column",
        gap:"32px"
    }} onInput={handleUserInput} >
    <Input id="name" placeholder={'Name'} type="text" value={user.name}></Input>
    <Input id="emailId" placeholder={'EmailId'} type="email" value={user.emailId}></Input>
    <Input id="password" placeholder={'Password'} type="password" value={user.password}></Input>
    </div>
    <Button height={'78px'} colorScheme="blue" onClick={signUpUser}>SignUp</Button>
    </Box>
    ) 
}

export default SignUp;