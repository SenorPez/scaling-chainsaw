import {Injectable} from '@angular/core';
import {JsonReturn} from './character';
import {Observable} from 'rxjs';

import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CharacterService {
  private characterUrl = 'https://www.loot.senorpez.com/campaigns/1/characters';

  constructor(private http: HttpClient) { }

  getCharacters(): Observable<JsonReturn> {
    return this.http.get<JsonReturn>(this.characterUrl);
  }
}
