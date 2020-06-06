export interface JsonReturn {
  _embedded: CharacterList;
}

export interface CharacterList {
  'loot-api:character': Character[];
}

export interface Character {
  id: number;
  name: string;
}
