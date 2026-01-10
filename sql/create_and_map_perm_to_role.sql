-- Given the desire to create a new permission and map it to some role, this script will do that.

-- Phase 1: Make the Permission

DECLARE
    v_key         NVARCHAR2(100) := 'ARCHIVE_PRODUCT';
    v_description NVARCHAR2(100) := 'Archive a Product';
    v_domain      NVARCHAR2(100) := 'PRODUCT';
BEGIN
    CREATE_PERMISSION(v_key, v_description, v_domain);
END;
/

-- Find Required Information

-- Paste permission key here
select * from BIZ_PERMISSION p where p.KEY = 'ARCHIVE_PRODUCT'; /

-- Paste company name here
select * from BIZ_COMPANY c where c.name = 'Test Company One'; /

-- Paste role name and company id here
select * from BIZ_ROLE r where r.name = 'Company Owner' and r.COMPANY_ID = '9D0EBF45842B472590BC87AA914C293E'; /

-- Phase 2: Map the Permission
DECLARE
    v_perm_id NVARCHAR2(100) := '480C91BA9AF9FB4FE063F15B000A934C';
    v_role_id NVARCHAR2(100) := '9D696181D9C44519A1B8EBF0E0642B68';
    
begin
    MAP_PERM_TO_ROLE(v_perm_id, v_role_id);
end; /

select * from JOIN_ROLE_PERMISSION rp where rp.PERMISSION_ID = '480C91BA9AF9FB4FE063F15B000A934C' and rp.ROLE_ID = '9D696181D9C44519A1B8EBF0E0642B68';


