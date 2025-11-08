import React, { useContext, useState } from 'react';
import { Box, Radio, RadioGroup, FormControl, FormLabel, Input, Button, useDisclosure, Modal, ModalOverlay, ModalContent, ModalHeader, ModalCloseButton, ModalBody, ModalFooter } from '@chakra-ui/react';
import { AuthContext } from '../context/AuthContext';


function Login() {
  const { login, signup } = useContext(AuthContext);
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [option, setOption] = useState('login');
  const [name, setName] = useState('');
  const [surname, setSurname] = useState('');
  const [password, setPassword] = useState('');
  const [afm, setAfm] = useState('');

  const handleOptionChange = (value) => setOption(value);
  const handleNameChange = (event) => setName(event.target.value);
  const handleSurnameChange = (event) => setSurname(event.target.value);
  const handlePasswordChange = (event) => setPassword(event.target.value);
  const handleAfmChange = (event) => setAfm(event.target.value);


  const submitForm = () => {
    if(option === 'login') {
      login({ name, surname, password });
    } else {
      signup({ name, surname, password, afm });
    }
  }

  return (
    <>

      <Modal isOpen={true} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>{option === 'login' ? 'Login' : 'Sign Up'}</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <RadioGroup onChange={handleOptionChange} value={option}>
              <Radio value="login">Login</Radio>
              <Radio value="signup">Sign Up</Radio>
            </RadioGroup>

            <FormControl mt={4}>
              <FormLabel>Name</FormLabel>
              <Input type="text" value={name} onChange={handleNameChange} />
            </FormControl>

            <FormControl mt={4}>
              <FormLabel>Surname</FormLabel>
              <Input type="text" value={surname} onChange={handleSurnameChange} />
            </FormControl>

            <FormControl mt={4}>
              <FormLabel>Password</FormLabel>
              <Input type="password" value={password} onChange={handlePasswordChange} />
            </FormControl>

            {option === 'signup' && (
              <FormControl mt={4}>
                <FormLabel>AFM</FormLabel>
                <Input type="text" value={afm} onChange={handleAfmChange} />
              </FormControl>
            )}
          </ModalBody>

          <ModalFooter>
            <Button colorScheme="blue" mr={3} onClick={onClose}>
              Close
            </Button>
            <Button variant="ghost" onClick={submitForm}>{option === 'login' ? 'Login' : 'Sign Up'}</Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </>
  );
}

export default Login;