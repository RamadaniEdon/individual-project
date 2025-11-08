import React, { createContext, useState, useEffect } from 'react';
import { loginReq } from '../services/api';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(localStorage.getItem('token') || '');

    useEffect(() => {
        const localToken = localStorage.getItem('token');
        if (localToken) {
            setToken(localToken);
            
        }
    }, []);

    const login = ({ name, surname, password }) => {

        loginReq({ name, surname, password }).then((token) => {
            setToken(token);
            localStorage.setItem('token', token);
            window.location.href = '/userdata';
            
        });
    };

    const signup = ({ name, surname, password, afm }) => {
        setToken(token);
        localStorage.setItem('token', token);
    };

    return (
        <AuthContext.Provider value={{ token, login, signup }}>
            {children}
        </AuthContext.Provider>
    );
}