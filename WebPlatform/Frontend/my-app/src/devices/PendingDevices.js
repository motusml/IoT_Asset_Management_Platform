import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import DialogActions from '@mui/material/DialogActions';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Title from './Title';
import IconButton from '@mui/material/IconButton';
import AddIcon from '@mui/icons-material/Add';
import Network from './DevicesGraph';
import CircularProgress from '@mui/material/CircularProgress';

export default class Devices extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            open: false,
            deviceData: { id: '', name: '', place: '', type: '', status: ''},
            pendingDevices: [],
            unregisteredDevices: 0,
            dialogStep: 0,
            hoveredRow: null,
            connectionName: '',
            isLoading: false,
        };
    }
    componentDidUpdate(prevState) {
        if (this.state.unregisteredDevices !== prevState.unregisteredDevices) {
            this.props.onDevicesChange(this.state.unregisteredDevices);
        }
    }

    async registerDevice(id, place, type, status) {
        const deviceDTO = {
            place: place,
            type: type,
            status: status
        };

        const response = await fetch(`/api/registerDevice?id=${id}`, {
            method: 'POST',
            credentials: 'include', // Include cookies in the request
            mode : 'cors',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(deviceDTO),
        });

        if (!response.ok) {
            console.log('Failed to register device');
        }
        
    }
    handleNodeClick = async(nodeId) => {
        console.log(nodeId);
        console.log(this.state.deviceData.id);
        const connectionName = window.prompt("Please enter the connection name");
        if (connectionName) {
            this.setState({ connectionName });
        
            const relationships = {
                "relationships": {
                [nodeId]: connectionName}
            };
            console.log(relationships);
            const response = await fetch(`/api/addRelationships?assetId=${this.state.deviceData.id}`, {
                method: 'POST',
                credentials: 'include', // Include cookies in the request
                mode : 'cors',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(relationships),
            });

            if (!response.ok) {
                console.log('Failed to add relationship to device');
            }
        }
    };

    handleOpen = (id, name) => {
        this.setState({ deviceData: { ...this.state.deviceData, id: id, name: name }, open: true });
    };

    handleClose = () => {
        this.setState({ open: false });
        this.setState({ dialogStep: 0 });
    };

    handleChange = (event) => {
        this.setState({ deviceData: { ...this.state.deviceData, [event.target.name]: event.target.value } });
    };

    handleRegister = () => {
        this.registerDevice(this.state.deviceData.id, this.state.deviceData.place, this.state.deviceData.type, this.state.deviceData.status);
        this.handleClose();
    };

    handleNext = () => {
        this.setState(prevState => ({ dialogStep: prevState.dialogStep + 1 }));
    };

    async componentDidMount() {
        this.setState({ isLoading: true });
        const response = await fetch('/api/getAllUnregisteredDevices', {
            method: 'GET',
            credentials: 'include', // Include cookies in the request
            mode : 'cors',
        });
        const data = await response.json();
        console.log(data);
        this.setState({ pendingDevices: data ,unregisteredDevices: data.length, isLoading: false});
        console.log(this.state.pendingDevices);
    }

    render() {
        return (
            <React.Fragment>
                <div style={{ height: '500px', overflow: 'auto' }}>
                    <Title>Unregistered Devices</Title>
                    {this.state.unregisteredDevices === 0 ? (
                        <p>No devices to register</p>
                    ) : (
                        <>
                            {this.state.isLoading && <CircularProgress />}
                            {!this.state.isLoading && (
                                <Table size="small">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell><strong>ID</strong></TableCell>
                                            <TableCell><strong>Name</strong></TableCell>
                                            <TableCell><strong>Status</strong></TableCell>
                                            <TableCell><strong>Last Modified</strong></TableCell>
                                            <TableCell align="right"><strong>Register</strong></TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                    {this.state.pendingDevices && this.state.pendingDevices.length > 0 ? (
                                        this.state.pendingDevices.map((device, index) => {
                                            return (
                                                <TableRow
                                                    key={device.id}
                                                    onMouseEnter={() => this.setState({ hoveredRow: index })}
                                                    onMouseLeave={() => this.setState({ hoveredRow: null })}
                                                    style={{ backgroundColor: this.state.hoveredRow === index ? '#f5f5f5' : 'inherit' }}
                                                >
                                                    <TableCell>{index + 1}</TableCell>
                                                    <TableCell>{device.name}</TableCell>
                                                    <TableCell>
                                                        <span style={{ color: device.status === 'Connected' ? 'green' : 'red' }}>●</span>
                                                    </TableCell>
                                                    <TableCell>{device.lastModified}</TableCell>
                                                    <TableCell align="right">
                                                        <IconButton color="primary" onClick={() => this.handleOpen(device.id, device.name)}>
                                                            <AddIcon />
                                                        </IconButton>
                                                    </TableCell>
                                                </TableRow>
                                            );
                                        })
                                    ) : (
                                        <p>Nessun dispositivo in sospeso.</p>
                                    )}
                                    </TableBody>
                                </Table>
                            )}
                        </>
                    )}
                </div>
                { this.state.open &&
                    <Dialog open={this.state.open} onClose={this.handleClose}>
                    {this.state.dialogStep === 0 && (
                        <>
                            <DialogTitle>Register Device</DialogTitle>
                            <DialogContent>
                                <TextField name="id" label="ID" value={this.state.deviceData.id} onChange={this.handleChange} fullWidth />
                                <TextField name="name" label="Name" value={this.state.deviceData.name} onChange={this.handleChange} fullWidth />
                                <TextField name="place" label="Place" value={this.state.deviceData.place} onChange={this.handleChange} fullWidth />
                                <TextField name="type" label="Type" value={this.state.deviceData.type} onChange={this.handleChange} fullWidth />
                                <TextField name="status" label="Status" value={this.state.deviceData.status} onChange={this.handleChange} fullWidth />
                            </DialogContent>
                            <DialogActions>
                                <Button onClick={this.handleClose}>Cancel</Button>
                                <Button onClick={this.handleNext}>Next</Button>
                            </DialogActions>
                        </>
                    )}
                    {this.state.dialogStep === 1 && (
                        <>
                            <DialogTitle>Select the links</DialogTitle>
                            <DialogContent>
                                <Network onNodeClick={this.handleNodeClick}/>
                            </DialogContent>
                            <DialogActions>
                                <Button onClick={this.handleClose}>Cancel</Button>
                                <Button onClick={this.handleRegister}>Register</Button>
                            </DialogActions>
                        </>
                    )}
                </Dialog>
                }
            </React.Fragment>
            
        );
    }
}