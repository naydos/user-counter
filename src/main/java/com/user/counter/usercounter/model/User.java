package com.user.counter.usercounter.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @EqualsAndHashCode.Include
    private String email;

    @EqualsAndHashCode.Include
    private String phoneNumber;

    private String source;

}
