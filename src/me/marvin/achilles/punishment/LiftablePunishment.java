package me.marvin.achilles.punishment;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import me.marvin.achilles.utils.UUIDConverter;
import me.marvin.achilles.utils.sql.BatchContainer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
public abstract class LiftablePunishment extends Punishment {
    protected UUID liftedBy;
    protected String liftReason;
    protected long liftedOn;
    protected boolean active;

    protected abstract void liftPunishment();
    public abstract BatchContainer createLiftBatch();

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.server = rs.getString("server");
        this.issuer = UUIDConverter.from(rs.getBytes("issuer"));
        this.target = UUIDConverter.from(rs.getBytes("target"));
        this.issueReason = rs.getString("issueReason");
        this.issuedOn = rs.getLong("issuedOn");
        this.active = rs.getBoolean("active");
        this.id = rs.getInt("id");
        if (!this.active) {
            this.liftedBy = UUIDConverter.from(rs.getBytes("liftedBy"));
            this.liftReason = rs.getString("liftReason");
            this.liftedOn = rs.getLong("liftedOn");
        }
    }

    public final void lift() {
        Preconditions.checkNotNull(liftedBy, "liftedby was null");
        Preconditions.checkNotNull(liftReason, "liftreason was null");
        this.liftedOn = System.currentTimeMillis();
        this.active = false;
        this.liftPunishment();
    }
}
