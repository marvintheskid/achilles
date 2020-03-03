package me.marvin.achilles.punishment;

import lombok.Getter;
import lombok.Setter;
import me.marvin.achilles.utils.UUIDConverter;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Copyright (c) 2019 marvintheskid (Kovács Márton)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

@Getter
@Setter
public abstract class ExpirablePunishment extends LiftablePunishment {
    public static int PERMANENT_PUNISHMENT = -1;

    protected long until;

    protected abstract void expirePunishment();

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        super.fromResultSet(rs);
        this.until = rs.getLong("until");
    }

    public final void expire() {
        this.active = false;
        expirePunishment();
    }

    public final boolean isPermanent() {
        return until == PERMANENT_PUNISHMENT;
    }

    public final boolean isTemporary() {
        return !isPermanent();
    }

    public final boolean isExpired() {
        return !isPermanent() && getRemaining() == 0;
    }

    public final long getRemaining() {
        if (isPermanent()) {
            return PERMANENT_PUNISHMENT;
        } else {
            if (until <= System.currentTimeMillis()) {
                return 0;
            } else {
                return until - System.currentTimeMillis();
            }
        }
    }
}
