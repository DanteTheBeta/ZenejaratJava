package com.zenejarat.backend.model;

// Ez az enum a felhasználók szerepköreit (jogosultsági szintjeit) tartalmazza.
public enum Role {
    ROLE_USER,  // Ezt a szerepet kapja minden új felhasználó alapértelmezetten.
    ROLE_ADMIN  // Ez a szerep az adminisztrátorokra vonatkozik.
}
