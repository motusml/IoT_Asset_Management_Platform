import React, { useEffect, useState } from 'react';
import Graph from "../components/Graph";
import { CssBaseline, Box, Toolbar, MenuItem, Fab, Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, Select } from '@mui/material';
import AddIcon from "@mui/icons-material/Add";
import CustomThemeProvider from '../components/ThemeProvider';
import AppBarComponent from '../components/AppBarComponent';
import DrawerComponent from '../components/DrawerComponent';

function Assets() {

    const userDataString = sessionStorage.getItem('userData');
    const userData = JSON.parse(userDataString);

    // Check if user data exists in session storage
    if (!userData) {
        // Redirect to sign-in page if user data is not present
        window.location.href = '/';
    }

    const [nodes, setNodes] = useState([]);
    const [links, setLinks] = useState([]);
    const [open, setOpen] = useState(false);
    const [anchorEl, setAnchorEl] = useState(null);
    const [addOpen, setAddOpen] = useState(false);
    const [name, setName] = useState('');
    const [label, setLabel] = useState('MonitoringTarget'); // Default label

    const toggleDrawer = () => {
        setOpen(!open); // Toggle the value of `open`
    };

    const handleMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        const response = await fetch('/api/getNetwork', {
            method: 'GET',
            credentials: 'include',
            mode: 'cors',
        });

        const jsonData = await response.json();
        if (jsonData == null || jsonData.nodes == null)
            return;

        const unchecked_links = jsonData.links;
        const nodeIds = jsonData.nodes.map(node => node.id);
        const filtered_links = unchecked_links.filter(link => {
            // Check if both link.source and link.target exist in the nodeIds array
            const sourceExists = nodeIds.includes(link.source);
            const targetExists = nodeIds.includes(link.target);

            return sourceExists && targetExists;
        });

        setNodes(jsonData.nodes);
        setLinks(filtered_links);
    };

    const addRelationship = async (assetId, relationships) => {
        const url = `/api/addRelationships?assetId=${assetId}`;
        try {
            const response = await fetch(url, {
                method: 'POST',
                credentials: 'include',
                mode: 'cors',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(relationships)
            });
            if (response.ok) {
                console.log('Relationship added successfully');
            } else {
                console.error('Failed to add relationship');
            }
        } catch (error) {
            console.error('Error adding relationship:', error);
        }
        fetchData();
    };

    const openModal = () => {
        setAddOpen(true);
    };

    const handleAddAsset = async () => {
        try {
            const response = await fetch(`/api/addAsset?name=${name}&label=${label}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            });

            if (response.ok) {
                // Asset added successfully, do something (e.g., close modal)
                console.log('Asset added successfully');
                handleCloseAdd(); // Close modal after successful addition
                fetchData();
            } else {
                console.error('Failed to add asset');
            }
        } catch (error) {
            console.error('Error adding asset:', error);
        }
    };

    const handleCloseAdd = () => {
        setAddOpen(false);
    };

    const handleLogout = () => {
        // Cancella i dati dello user dalla sessione
        sessionStorage.removeItem('userData');
        // Reindirizza l'utente alla pagina di login
        window.location.href = '/signin';
    };

    const deleteNode = async (id) => {
        try {
            const response = await fetch(`/api/deleteAsset?assetId=${id}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            });

            if (response.ok) {
                console.log('Asset deleted successfully');
                fetchData();
            } else {
                const errorMessage = await response.text();
                console.error('Failed to delete asset:', errorMessage);
                alert('Failed to delete asset: first delete the links!');
            }
        } catch (error) {
            console.error('Error deleting asset:', error);
        }
    };

    const deleteLink = async (id) => {
        try {
            const response = await fetch(`/api/deleteRelationship?relId=${id}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            });

            if (response.ok) {
                console.log('Link deleted successfully');
                fetchData();
            } else {
                console.error('Failed to delete link');
            }
        } catch (error) {
            console.error('Error deleting asset:', error);
        }
    };

    return (
        <CustomThemeProvider>
            <CssBaseline />
            <AppBarComponent
                pageTitle={'Assets'}
                open={open}
                toggleDrawer={toggleDrawer}
                anchorEl={anchorEl}
                handleMenu={handleMenu}
                handleClose={handleClose}
                openMenu={Boolean(anchorEl)}
                handleLogout={handleLogout}
            />
            <div style={{position: 'relative', width: '100%', height: '100%'}}>
                <DrawerComponent
                    open={open}
                    toggleDrawer={toggleDrawer}
                />
                <div style={{position: 'absolute', top: 0, left: 0, width: '100%', height: '100%'}}>
                    {nodes.length > 0 ? (
                        <Graph nodes={nodes} links={links} deleteNode={deleteNode} deleteLink={deleteLink} addRelationship={addRelationship} style={{width: '100%', height: '100%'}}/>
                    ) : (
                        <p></p>
                    )}
                </div>
            </div>
            <Fab
                color="primary"
                aria-label="add"
                sx={{
                    position: 'absolute',
                    bottom: '20px',
                    right: '20px',
                }}
                onClick={openModal}
            >
                <AddIcon/>
            </Fab>
            {/* Dialog for adding a new asset */}
            <Dialog open={addOpen} onClose={handleCloseAdd}>
                <DialogTitle>Add New Asset</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        id="name"
                        label="Name"
                        type="text"
                        fullWidth
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                    <Select
                        value={label}
                        onChange={(e) => setLabel(e.target.value)}
                        fullWidth
                        label="Label"
                    >
                        <MenuItem value="MonitoringTarget">Monitoring Target</MenuItem>
                        <MenuItem value="Sensor">Sensor</MenuItem>
                        <MenuItem value="Gateway">Gateway</MenuItem>
                    </Select>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseAdd}>Cancel</Button>
                    <Button onClick={handleAddAsset}>Add</Button>
                </DialogActions>
            </Dialog>

        </CustomThemeProvider>
    );
}

export default Assets;
