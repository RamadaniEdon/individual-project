import React, { createContext, useState, useEffect } from 'react';
import { getClassesAndProperties } from '../services/api';


export const EntityContext = createContext();

export const EntityProvider = ({ children }) => {
    const [entities, setEntities] = useState([]);

    useEffect(() => {
        const localEntities = localStorage.getItem('entities');
        if (localEntities) {
            setEntities(JSON.parse(localEntities));

        }
        else {
            getClassesAndProperties().then((data) => {
                setEntities(data);
                localStorage.setItem('entities', JSON.stringify(data));
            });
        }

    }, []);

    const getClass = (className) => {
        let result;
        entities.forEach((entity) => {
            if (entity.className == className) {
                result = entity;
            }
        });
        return result;
    };

    return (
        <EntityContext.Provider value={{ entities, getClass }}>
            {children}
        </EntityContext.Provider>
    );
}