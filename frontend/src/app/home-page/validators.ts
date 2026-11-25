import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

const IPV4_REGEX =
  /^(25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)){3}$/;
const MAC_REGEX = /^([0-9A-Fa-f]{2}([:-])){5}[0-9A-Fa-f]{2}$/;

export function ipAddressValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (value === null || value === undefined || value === '') {
      return null;
    }
    const trimmed = String(value).trim();
    return IPV4_REGEX.test(trimmed) ? null : { ipAddress: { value: trimmed } };
  };
}

export function macAddressValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (value === null || value === undefined || value === '') {
      return null;
    }
    const trimmed = String(value).trim();
    return MAC_REGEX.test(trimmed) ? null : { macAddress: { value: trimmed } };
  };
}