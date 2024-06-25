import React, { useState, useEffect } from 'react';
import { CssBaseline, Typography, MenuItem, Select, Button, Box, Toolbar, Grid, Paper } from '@mui/material';
import CustomThemeProvider from '../components/ThemeProvider';
import AppBarComponent from '../components/AppBarComponent';
import DeviceDataReports from "./DeviceDataReports";
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import DrawerComponent from "../components/DrawerComponent";
import Container from "@mui/material/Container";

const Reports = () => {
    const [open, setOpen] = useState(true);
    const [anchorEl, setAnchorEl] = useState(null);
    const [devices, setDevices] = useState([]);
    const [selectedDeviceId, setSelectedDeviceId] = useState("");
    const [pendingData, setPendingData] = useState(false);
    const [measurements, setMeasurements] = useState([]);

    const userDataString = sessionStorage.getItem('userData');
    const userData = JSON.parse(userDataString);

    // Check if user data exists
    if (!userData) {
        // Redirect to sign-in page if user data is not present
        window.location.href = '/';
    }

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch('/api/getAllRegisteredDevices', {
                    method: 'GET',
                    credentials: 'include',
                    headers: { 'Content-Type': 'application/json' }
                });
                const data = await response.json();
                const devices = JSON.parse(JSON.parse(data)).map(device => JSON.parse(device));
                setDevices(devices);
                console.log(devices);
            } catch (error) {
                console.error('Error fetching devices:', error);
            }
        };

        fetchData();
    }, []);

    useEffect(() => {
        const fetchPendingData = async () => {
            if (selectedDeviceId) {
                try {
                    const response = await fetch(`/api/getAsset?id=${selectedDeviceId}`, {
                        method: 'GET',
                        credentials: 'include',
                        headers: { 'Content-Type': 'application/json' }
                    });
                    const deviceData = await response.json();
                    const pending = deviceData.asset.properties.pendingData;
                    setPendingData(pending);

                    if (pending) {
                        const interval = setInterval(() => {
                            window.location.reload();
                        }, 15000);
                        return () => clearInterval(interval);
                    }
                } catch (error) {
                    console.error('Error fetching pending data:', error);
                }
            }
        };

        fetchPendingData();
    }, [selectedDeviceId]);

    useEffect(() => {
        const fetchMeasurements = async () => {
            if (selectedDeviceId) {
                try {
                    const response = await fetch(`/api/retrieveDeviceDataMeasurements?deviceId=${selectedDeviceId}`, {
                        method: 'GET',
                        credentials: 'include',
                        headers: { 'Content-Type': 'application/json' }
                    });
                    const measurements = await response.json();
                    setMeasurements(measurements);
                    console.log('Measurements:', measurements);
                } catch (error) {
                    console.error('Error fetching measurements:', error);
                }
            }
        };

        fetchMeasurements();
    }, [selectedDeviceId]);

    const handleMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const toggleDrawer = () => {
        setOpen(!open);
    };

    const handleChange = (event) => {
        setSelectedDeviceId(event.target.value);
    };

    const handleLogout = () => {
        sessionStorage.removeItem('userData');
        window.location.href = '/signin';
    };

    const handleRequestNewData = async () => {
        try {
            const response = await fetch(`/api/updateData?deviceId=${selectedDeviceId}`, {
                method: 'POST',
                credentials: 'include',
            });
            if (response.ok) {
                setPendingData(true);
            }
        } catch (error) {
            console.error('Error requesting new data:', error);
        }
    };

    return (
        <CustomThemeProvider>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
                <Box sx={{ display: 'flex' }}>
                    <CssBaseline />
                    <AppBarComponent
                        pageTitle={'Device Data Reports'}
                        open={open}
                        toggleDrawer={toggleDrawer}
                        anchorEl={anchorEl}
                        handleMenu={handleMenu}
                        handleClose={handleClose}
                        openMenu={Boolean(anchorEl)}
                        handleLogout={handleLogout}
                    />
                    <DrawerComponent open={open} toggleDrawer={toggleDrawer} />
                    <Box component="main" sx={{ backgroundColor: (theme) => theme.palette.mode === 'light' ? theme.palette.grey[100] : theme.palette.grey[900], flexGrow: 1, height: '100vh', overflow: 'auto' }}>
                        <Toolbar />
                        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
                            <Grid container spacing={3}>
                                <Grid item xs={12}>
                                    <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column' }}>
                                        <Select
                                            value={selectedDeviceId}
                                            onChange={handleChange}
                                            displayEmpty
                                            inputProps={{ 'aria-label': 'Without label' }}
                                        >
                                            <MenuItem value="">
                                                <em>None</em>
                                            </MenuItem>
                                            {devices.map((device) => (
                                                <MenuItem key={device.id} value={device.id}>{device.name}</MenuItem>
                                            ))}
                                        </Select>
                                        {selectedDeviceId && (
                                            <>
                                                <Button
                                                    variant="contained"
                                                    color="primary"
                                                    onClick={handleRequestNewData}
                                                    disabled={pendingData}
                                                    sx={{ mt: 2 }}
                                                >
                                                    REQUEST NEW DATA
                                                </Button>
                                                {pendingData && <Typography sx={{ mt: 2 }}>New data requested from the device</Typography>}
                                                {measurements.length === 0 ? (
                                                    <Typography sx={{ mt: 2 }}>No data available, please request new data</Typography>
                                                ) : (
                                                    <DeviceDataReports deviceId={selectedDeviceId} measurements={measurements} />
                                                )}
                                            </>
                                        )}
                                    </Paper>
                                </Grid>
                            </Grid>
                        </Container>
                    </Box>
                </Box>
            </LocalizationProvider>
        </CustomThemeProvider>
    );
};

export default Reports;
