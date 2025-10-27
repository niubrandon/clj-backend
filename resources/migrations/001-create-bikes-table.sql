-- Create bikes table
CREATE TABLE IF NOT EXISTS bikes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    year INTEGER NOT NULL,
    color VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on brand for faster queries
CREATE INDEX IF NOT EXISTS idx_bikes_brand ON bikes(brand);

-- Create index on year for filtering
CREATE INDEX IF NOT EXISTS idx_bikes_year ON bikes(year);

