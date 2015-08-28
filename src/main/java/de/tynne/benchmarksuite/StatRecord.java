/*
 * Copyright 2015 Stephan Fuhrmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tynne.benchmarksuite;

import lombok.Getter;

/**
 * Statistic moments of measured data.
 * @author Stephan Fuhrmann
 */
public class StatRecord {

    @Getter
    private double min;
    @Getter
    private double max;
    @Getter
    private double sum;
    @Getter
    private int count;

    /** Scales the numbers in the record with the given double.
     * @param scale the scale to multiply with.
     * @return a new instance with the data of {@code this} record
     * scaled with the given scale.
     */
    public StatRecord scaleTo(double scale) {
        StatRecord record = new StatRecord();
        record.min = min * scale;
        record.max = max * scale;
        record.sum = sum * scale;
        return record;
    }

    /** Adds a new measurement to the statistic record.
     * @param value the value to add to the stat record.
     */
    public void put(long value) {

        if (count != 0) {
            min = Math.min(value, min);
            max = Math.max(value, max);
        } else {
            min = value;
            max = value;
        }
        sum += value;
        count ++;
    }

    /** Returns the average of the seen values. */
    public double getAverage() {
        return (double)sum / (double)count;
    }

    /** Resets the record as if it never received data. */
    public void reset() {
        min = 0;
        max = 0;
        sum = 0;
        count = 0;
    }
}
