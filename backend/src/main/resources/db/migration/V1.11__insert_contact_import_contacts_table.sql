INSERT INTO contact_import_contacts (
    contact_import_id,
    contact_id,
    created_at
)
SELECT
    contact_import_id,
    id,
    created_at
FROM contacts;
