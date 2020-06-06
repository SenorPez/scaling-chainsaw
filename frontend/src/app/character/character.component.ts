import { Component, OnInit, Input } from '@angular/core';
import {Character} from '../characters';

@Component({
  selector: 'app-character',
  templateUrl: './character.component.html',
  styleUrls: ['./character.component.css']
})
export class CharacterComponent implements OnInit {
  @Input() character: Character;

  constructor() { }

  ngOnInit(): void {
  }

}
