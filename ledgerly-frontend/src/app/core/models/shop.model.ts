export interface Shop {
    id: number;
    name: string;
    description: string;
    address: string;
    phoneNumber: string;
    email: string;
    gstNumber?: string;
    panNumber?: string;
    city: string;
    state: string;
    pincode: string;
    active: boolean;
    ownerId: number;
    ownerName: string;
}