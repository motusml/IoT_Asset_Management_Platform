import React from 'react';

const Stats = ({ registeredDevices, unregisteredDevices }) => {
    console.log(registeredDevices); // dovrebbe stampare l'array di dispositivi registrati
    console.log(unregisteredDevices); 
    return (
        <div>
            <h2>Statistics</h2>
            <p>Registered devices: {registeredDevices}</p>
            <p>Unregistered devices: {unregisteredDevices}</p>
        </div>
    );
};

export default Stats;