/*
 * Copyright 2017 ING Bank N.V.
 * This file is part of the go-ethereum library.
 *
 * The go-ethereum library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The go-ethereum library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the go-ethereum library. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.kit.prover.zeroknowledge.prover;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config extends Properties {

    private static final String PROPERTY_FILE_NAME = "config.properties";
    private static Config instance = new Config();

    private Config() {

        final InputStream configStream = this.getClass().getClassLoader()
                                        .getResourceAsStream(PROPERTY_FILE_NAME);

        try {
            this.load(configStream);
            configStream.close();
        } catch (IOException ioe) {
            System.err.println("Cannot read " + PROPERTY_FILE_NAME + ", " + ioe.getMessage());
        }
    }

    public static Config getInstance() {
        return instance;
    }

    @Override
    public String getProperty(final String propertyName) {

        final String property = super.getProperty(propertyName);

        if (property == null || property.isEmpty()) {
            System.err.println("Property " + propertyName + ", not configured in: " + PROPERTY_FILE_NAME);
        }
        return property;
    }
}
