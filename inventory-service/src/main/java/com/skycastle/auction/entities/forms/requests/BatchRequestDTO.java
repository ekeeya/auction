package com.skycastle.auction.entities.forms.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class BatchRequestDTO<T>  implements Serializable {

    private static final long serialVersionUID = 2925352260841507975L;
    private List<T> entries = new ArrayList<>();

    public BatchRequestDTO(List<T> entries) {
        this.setEntries(entries);
    }
}
