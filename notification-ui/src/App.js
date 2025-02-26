import logo from './logo.svg';
import './App.css';
import Login from './login/Login';
import { Button, Flex, Text } from '@chakra-ui/react';
import SignUp from './signUp/SignUp';
import { Routes, Route } from 'react-router-dom';
import Preference from './preferences/Preference';

function App() {
  return (
   <Flex width={'100%'} height={'100%'}>
    <Flex width={'10%'}>
    
    </Flex>
    <Flex width={'80%'} gap={'16px'} direction={'column'}>
    
      <Flex gap={'16px'}>
      <Routes>
        <Route path='/' element={<> 
        <Text fontSize={'3xl'}>User Enrollment</Text>
        <Text fontSize={'1xl'}>Please follow the steps below to create an account</Text>     
        <SignUp/>
        <Login/></>}/> 
        <Route path='/preferences' element={
        <Preference/>
          } />
      </Routes>
      </Flex>
   </Flex>
   <Flex width={'10%'}>
    
   </Flex>
   </Flex>
  );
}

export default App;
