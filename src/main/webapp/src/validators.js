
export function isEmail(email) {
    const re = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/;
    return re.test(email);
}

export function isZip(zip) {
    const re = /^\d{3} ?\d{2}$/;
    return re.test(zip);
}