/*
 * Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.digdir.dpi.client.internal.domain;


import java.util.Arrays;
import java.util.Optional;

/**
 * Defines validation modes available as part of this package.
 *
 * @author erlend
 */
public enum Mode {

    @RecipePath("/pki/recipe-dpi-self-signed.xml")
    SELF_SIGNED,

    @RecipePath("/pki/recipe-dpi-norway-test.xml")
    TEST,

    @RecipePath("/pki/recipe-dpi-norway-production.xml")
    PRODUCTION;

    /**
     * Fetches {@link Mode} by comparing name using String#equalsIgnoreCase.
     *
     * @param value Some string.
     * @return Mode if found.
     */
    public static Optional<Mode> of(String value) {
        return Arrays.stream(values())
                .filter(mode -> mode.name().equalsIgnoreCase(value))
                .findAny();
    }
}
