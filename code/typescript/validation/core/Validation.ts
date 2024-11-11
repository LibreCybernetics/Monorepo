export class Valid<A> {
    constructor(readonly content: A) {}
}

export class Invalid<Error> {
    constructor(readonly error: Set<Error>) {}
}

export type Validated<A, Error> = Valid<A> | Invalid<Error>;